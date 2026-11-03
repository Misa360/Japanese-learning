#!/bin/bash
# Kiem thu end-to-end qua HTTP (68 ca): dang ky, dang nhap, khoa tam, session fixation, open redirect, CSRF, doi mat khau,
# dang xuat, upload anh (anh gia/polyglot/qua lon), phan trang, dang ky khoa, hoan thanh bai, rate limit.
# Yeu cau: app dang chay tai $B (mac dinh http://localhost:8089/jlearn), DB da nap install.sql + seed/seed_demo_f10_f18.sql,
# lenh `mariadb -uroot jlearn` dung duoc, JLEARN_UPLOAD_DIR=/tmp/uploads, JLEARN_SECURITY_BCRYPT_COST=4 (cho nhanh),
# va file anh mau trong /tmp/e2e (chay: python3 src/test/e2e/make_fixtures.py). Moi lan chay can khoi dong lai app
# (rate limit dang ky nam trong bo nho). Chi dung tren DB thu, KHONG tren DB that.
B=http://localhost:8089/jlearn
PASS=0; FAIL=0
ok(){ if [ "$2" = "1" ]; then PASS=$((PASS+1)); else FAIL=$((FAIL+1)); echo "FAIL: $1"; fi; }
tok(){ grep -o 'name="csrf_token" value="[^"]*"' | head -1 | sed 's/.*value="//;s/"$//'; }
sid(){ grep -P 'JSESSIONID' "$1" | awk '{print $7}' | tail -1; }
J1=/tmp/e2e/j1; rm -f $J1*
U="u$RANDOM"; EM="$U@example.com"

# --- F01 register
T=$(curl -s -c $J1 $B/register | tok); ok "register form has csrf" $([ -n "$T" ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 -c $J1 -X POST -d "username=$U&email=$EM&password=Abcdef12&password_confirm=Abcdef12" $B/register); ok "POST without csrf -> 403" $([ "$C" = 403 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "username=a&email=bad&password=123&password_confirm=9" $B/register)
echo "$R" | grep -q 'Tên đăng nhập gồm 3–30' ; ok "username error shown" $([ $? = 0 ] && echo 1 || echo 0)
echo "$R" | grep -q 'Email không hợp lệ'; ok "email error shown" $([ $? = 0 ] && echo 1 || echo 0)
echo "$R" | grep -q 'cần ít nhất 8'; ok "weak password error" $([ $? = 0 ] && echo 1 || echo 0)
echo "$R" | grep -q 'value="bad"'; ok "form data retained" $([ $? = 0 ] && echo 1 || echo 0)
echo "$R" | grep -q 'value="9"'; ok "password not echoed" $([ $? != 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "username=student&email=zz@example.com&password=Abcdef12&password_confirm=Abcdef12" $B/register)
echo "$R" | grep -q 'Tên đăng nhập đã được sử dụng'; ok "duplicate username" $([ $? = 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "username=$U&email=student@example.com&password=Abcdef12&password_confirm=Abcdef12" $B/register)
echo "$R" | grep -q 'Email đã được sử dụng'; ok "duplicate email" $([ $? = 0 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code} %{redirect_url}" -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "username=$U&email=$EM&password=Abcdef12&password_confirm=Abcdef12&role_id=1&role=admin" $B/register)
ok "register ok -> redirect /login ($C)" $(echo "$C" | grep -q '302 .*/jlearn/login' && echo 1 || echo 0)
ROLE=$(mariadb -uroot jlearn -N -e "select r.name from users u join roles r on r.id=u.role_id where u.email='$EM'")
ok "new user role = user (role param ignored): $ROLE" $([ "$ROLE" = user ] && echo 1 || echo 0)
H=$(mariadb -uroot jlearn -N -e "select password_hash from users where email='$EM'"); ok "bcrypt hash stored" $(echo "$H" | grep -q '^\$2a\$' && echo 1 || echo 0)

# --- F02 login
C=$(curl -s -o /dev/null -w "%{http_code} %{redirect_url}" -b $J1 -c $J1 $B/login); T=$(curl -s -b $J1 -c $J1 $B/login | tok)
OLD=$(sid $J1)
R=$(curl -s -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "identifier=$EM&password=WrongPass1" $B/login)
echo "$R" | grep -q 'Sai tài khoản hoặc mật khẩu'; ok "generic login error" $([ $? = 0 ] && echo 1 || echo 0)
R2=$(curl -s -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "identifier=nosuch@example.com&password=WrongPass1" $B/login)
echo "$R2" | grep -q 'Sai tài khoản hoặc mật khẩu'; ok "same error for unknown user" $([ $? = 0 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code} %{redirect_url}" -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "identifier=$U&password=Abcdef12&next=//evil.com" $B/login)
ok "login by username, open redirect //evil.com blocked ($C)" $(echo "$C" | grep -q '302 http://localhost:8089/jlearn/dashboard' && echo 1 || echo 0)
NEW=$(sid $J1); ok "session id changed after login" $([ "$OLD" != "$NEW" ] && echo 1 || echo 0)
# old session id must not work
printf '# Netscape HTTP Cookie File\nlocalhost\tFALSE\t/jlearn\tFALSE\t0\tJSESSIONID\t%s\n' "$OLD" > /tmp/e2e/old.jar
C=$(curl -s -o /dev/null -w "%{http_code}" -b /tmp/e2e/old.jar $B/dashboard); ok "old session id not authenticated (code $C)" $([ "$C" = 302 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 $B/dashboard); echo "$R" | grep -q 'Xin chào'; ok "dashboard renders" $([ $? = 0 ] && echo 1 || echo 0)
echo "$R" | grep -q 'Xu hiện có'; ok "dashboard stats" $([ $? = 0 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 -c $J1 $B/login); ok "logged-in /login -> redirect (GuestFilter) $C" $([ "$C" = 302 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 $B/admin); ok "user -> /admin = 403 ($C)" $([ "$C" = 403 ] && echo 1 || echo 0)

# next= valid internal path (second user via fresh jar)
J2=/tmp/e2e/j2; rm -f $J2
curl -s -c $J2 -o /dev/null "$B/my-courses"; T2=$(curl -s -b $J2 -c $J2 "$B/login?next=/history" | tok)
C=$(curl -s -o /dev/null -w "%{redirect_url}" -b $J2 -c $J2 -X POST --data-urlencode "csrf_token=$T2" -d "identifier=$EM&password=Abcdef12" $B/login)
ok "return to page seen before login (session nextUrl=/my-courses) -> $C" $(echo "$C" | grep -q '/jlearn/my-courses$' && echo 1 || echo 0)

# --- lockout: 5 wrong -> locked even with right password
J3=/tmp/e2e/j3; rm -f $J3; T3=$(curl -s -c $J3 $B/login | tok)
for i in 1 2 3 4 5; do curl -s -o /dev/null -b $J3 -c $J3 -X POST --data-urlencode "csrf_token=$T3" -d "identifier=student@example.com&password=bad$i" $B/login; done
R=$(curl -s -b $J3 -c $J3 -X POST --data-urlencode "csrf_token=$T3" -d "identifier=student@example.com&password=password123" $B/login)
echo "$R" | grep -q 'thử lại sau'; ok "5 failures -> temporary lock, right password refused" $([ $? = 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J3 -c $J3 -X POST --data-urlencode "csrf_token=$T3" -d "identifier=STUDENT&password=password123" $B/login)
echo "$R" | grep -q 'thử lại sau'; ok "lock also applies via username form" $([ $? = 0 ] && echo 1 || echo 0)
mariadb -uroot jlearn -e "delete from login_attempts"

# --- F12 enroll + F15 + F07 + F09
T=$(curl -s -b $J1 -c $J1 "$B/courses/1" | tok)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" $B/courses/1/enroll); ok "enroll -> 302" $([ "$C" = 302 ] && echo 1 || echo 0)
curl -s -o /dev/null -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" $B/courses/1/enroll
UID_=$(mariadb -uroot jlearn -N -e "select id from users where email='$EM'")
N=$(mariadb -uroot jlearn -N -e "select count(*) from enrollments where user_id=$UID_"); ok "double enroll -> 1 row" $([ "$N" = 1 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 $B/learn/1); echo "$R" | grep -q 'yt-player'; ok "lesson page has player div" $([ $? = 0 ] && echo 1 || echo 0)
echo "$R" | grep -q 'dQw4w9WgXcQ'; ok "lesson video id rendered" $([ $? = 0 ] && echo 1 || echo 0)
T=$(echo "$R" | tok)
R=$(curl -s -b $J1 -c $J1 -X POST -H "X-CSRF-Token: $T" -H "X-Requested-With: XMLHttpRequest" -d "position_seconds=42" $B/learn/1/position); echo "$R" | grep -q '"saved":42'; ok "save position JSON ($R)" $([ $? = 0 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 -c $J1 -X POST -H "X-Requested-With: XMLHttpRequest" -d "position_seconds=43" $B/learn/1/position); ok "position without csrf -> 403" $([ "$C" = 403 ] && echo 1 || echo 0)
sleep 3.2; C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 -c $J1 -X POST -H "X-CSRF-Token: $T" -H "X-Requested-With: XMLHttpRequest" -d "position_seconds=-5" $B/learn/1/position); ok "negative position -> 400 ($C)" $([ "$C" = 400 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 -X POST -H "X-CSRF-Token: $T" -H "Accept: application/json" $B/learn/1/complete); echo "$R" | grep -q '"coinsAwarded":10'; ok "complete lesson +10 ($R)" $([ $? = 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 -X POST -H "X-CSRF-Token: $T" -H "Accept: application/json" $B/learn/1/complete); echo "$R" | grep -q '"coinsAwarded":0'; ok "complete again +0" $([ $? = 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 "$B/my-courses?status=in_progress"); echo "$R" | grep -q 'Tiếng Nhật N5 Cấp Tốc'; ok "my-courses in_progress shows course" $([ $? = 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 "$B/my-courses?status=completed"); echo "$R" | grep -q 'Tiếng Nhật N5 Cấp Tốc'; ok "my-courses completed hides it" $([ $? != 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 "$B/my-courses?status=%27%3Cscript%3E"); echo "$R" | grep -q '<script>'; ok "bad filter value safe" $([ $? != 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 "$B/history"); echo "$R" | grep -q 'Bài 1: Làm quen'; ok "history lists lesson" $([ $? = 0 ] && echo 1 || echo 0)
# pagination: insert 45 logs
mariadb -uroot jlearn -e "insert into study_logs (user_id,lesson_id,activity_type,logged_at) select $UID_, 2, 'lesson_view', NOW() - INTERVAL seq MINUTE from (select @r:=@r+1 as seq from information_schema.columns, (select @r:=0) x limit 45) t"
R=$(curl -s -b $J1 "$B/history?page=2"); echo "$R" | grep -q 'aria-current="page">2'; ok "history page 2 + pagination component" $([ $? = 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 "$B/history?page=999"); echo "$R" | grep -q 'aria-current="page">3'; ok "page beyond end clamps to last" $([ $? = 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 "$B/history?page=abc"); echo "$R" | grep -q 'aria-current="page">1'; ok "page=abc -> 1" $([ $? = 0 ] && echo 1 || echo 0)

# --- F08 profile upload
up(){ curl -s -b $J1 -c $J1 -F "csrf_token=$T" -F "full_name=Tên Mới" -F "target_level_id=$2" -F "avatar=@/tmp/e2e/$1;type=image/png" $B/profile; }
T=$(curl -s -b $J1 -c $J1 $B/profile | tok)
R=$(up ok.png 2); echo "$R" | grep -q 'ok.png' ; 
FN=$(mariadb -uroot jlearn -N -e "select avatar_path from users where id=$UID_"); ok "avatar stored with random name: $FN" $(echo "$FN" | grep -qE '^[0-9a-f]{32}\.png$' && echo 1 || echo 0)
ok "file exists outside webapp" $([ -f /tmp/uploads/$FN ] && echo 1 || echo 0)
NAME=$(mariadb -uroot --default-character-set=utf8mb4 jlearn -N -e "select full_name, target_level_id from users where id=$UID_"); ok "profile name+level saved ($NAME)" $(echo "$NAME" | grep -q 'Tên Mới	2' && echo 1 || echo 0)
C=$(curl -s -o /tmp/e2e/got.png -w "%{http_code} %{content_type}" $B/media/avatar/$FN); ok "avatar served ($C)" $(echo "$C" | grep -q '200 image/png' && echo 1 || echo 0)
for bad in fake.png gif.png big.png; do
  R=$(up $bad 2); echo "$R" | grep -q 'jl-field-error'; E=$?
  FN2=$(mariadb -uroot jlearn -N -e "select avatar_path from users where id=$UID_")
  ok "$bad rejected (error shown=$E) and avatar unchanged" $([ $E = 0 ] && [ "$FN2" = "$FN" ] && echo 1 || echo 0)
done
# polyglot (PNG hop le + payload PHP dinh kem): duoc chap nhan nhung payload bi loai vi anh duoc ghi lai
up poly.png 2 >/dev/null; FN=$(mariadb -uroot jlearn -N -e "select avatar_path from users where id=$UID_")
ok "polyglot png accepted but re-encoded: stored file has no payload" $(grep -q '<?php' /tmp/uploads/$FN && echo 0 || echo 1)
# polyglot check: nothing with <?php in uploads
ok "no php payload stored" $(grep -rl '<?php' /tmp/uploads >/dev/null 2>&1 && echo 0 || echo 1)
for p in '../../etc/passwd' '..%2f..%2fetc%2fpasswd' 'x.png' "$(echo $FN | sed 's/png/php/')"; do
  C=$(curl -s --path-as-is -o /dev/null -w "%{http_code}" "$B/media/avatar/$p"); ok "avatar path '$p' -> 404 ($C)" $([ "$C" = 404 ] || [ "$C" = 400 ] || [ "$C" = 302 ] && echo 1 || echo 0)
done
R=$(up ok.png 99); echo "$R" | grep -q 'Cấp độ không hợp lệ'; ok "invalid level rejected" $([ $? = 0 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 -c $J1 -F "full_name=x" -F "avatar=@/tmp/e2e/ok.png" $B/profile); ok "multipart without csrf -> 403" $([ "$C" = 403 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 -F "csrf_token=$T" -F "full_name=Hacker" -F "role_id=1" -F "coin_balance=999999" -F "status=locked" $B/profile >/dev/null; mariadb -uroot jlearn -N -e "select role_id, coin_balance, status from users where id=$UID_"); ok "role/coin/status not editable via profile ($R)" $(echo "$R" | grep -q '^2	10	active$' && echo 1 || echo 0)

# --- F04 change password
T=$(curl -s -b $J1 -c $J1 $B/account/password | tok); OLD=$(sid $J1)
R=$(curl -s -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "old_password=Wrong123x&new_password=Newpass99&new_password_confirm=Newpass99" $B/account/password); echo "$R" | grep -q 'Mật khẩu hiện tại không đúng'; ok "wrong old password" $([ $? = 0 ] && echo 1 || echo 0)
R=$(curl -s -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "old_password=Abcdef12&new_password=Abcdef12&new_password_confirm=Abcdef12" $B/account/password); echo "$R" | grep -q 'khác mật khẩu cũ'; ok "same as old rejected" $([ $? = 0 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" -d "old_password=Abcdef12&new_password=Newpass99&new_password_confirm=Newpass99" $B/account/password); ok "change ok -> 302" $([ "$C" = 302 ] && echo 1 || echo 0)
ok "session id changed after password change" $([ "$OLD" != "$(sid $J1)" ] && echo 1 || echo 0)
ok "still logged in" $([ "$(curl -s -o /dev/null -w '%{http_code}' -b $J1 -c $J1 $B/dashboard)" = 200 ] && echo 1 || echo 0)

# --- F03 logout
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 $B/logout); ok "GET /logout -> 405 ($C)" $([ "$C" = 405 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 -c $J1 -X POST $B/logout); ok "POST /logout without csrf -> 403" $([ "$C" = 403 ] && echo 1 || echo 0)
T=$(curl -s -b $J1 -c $J1 $B/dashboard | grep -o 'name="csrf_token" value="[^"]*"' | head -1 | sed 's/.*value="//;s/"$//')
cp $J1 /tmp/e2e/before_logout.jar
C=$(curl -s -o /dev/null -w "%{http_code} %{redirect_url}" -b $J1 -c $J1 -X POST --data-urlencode "csrf_token=$T" $B/logout); ok "POST /logout ok ($C)" $(echo "$C" | grep -q '302 .*/login' && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b /tmp/e2e/before_logout.jar $B/dashboard); ok "old session dead after logout ($C)" $([ "$C" = 302 ] && echo 1 || echo 0)
C=$(curl -s -o /dev/null -w "%{http_code}" -b $J1 $B/dashboard); ok "dashboard needs login again ($C)" $([ "$C" = 302 ] && echo 1 || echo 0)
# login with new password
J4=/tmp/e2e/j4; rm -f $J4; T4=$(curl -s -c $J4 $B/login | tok)
C=$(curl -s -o /dev/null -w "%{redirect_url}" -b $J4 -c $J4 -X POST --data-urlencode "csrf_token=$T4" -d "identifier=$EM&password=Newpass99" $B/login); ok "login with new password" $(echo "$C" | grep -q '/dashboard' && echo 1 || echo 0)

# --- register rate limit
for i in 1 2 3 4 5 6; do Tr=$(curl -s -c /tmp/e2e/j5 $B/register | tok); CODE=$(curl -s -o /dev/null -w "%{http_code}" -b /tmp/e2e/j5 -c /tmp/e2e/j5 -X POST --data-urlencode "csrf_token=$Tr" -d "username=x$i$RANDOM&email=x$i$RANDOM@e.com&password=Abcdef12&password_confirm=Abcdef12" $B/register); done
ok "register rate limit -> 429 after 5 per IP (last=$CODE)" $([ "$CODE" = 429 ] && echo 1 || echo 0)
echo "PASS=$PASS FAIL=$FAIL"
