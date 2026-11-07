package com.jlearn;

import com.jlearn.model.Pager;
import com.jlearn.util.PasswordHasher;
import com.jlearn.util.SafeRedirect;
import com.jlearn.util.Validator;

/** Kiem tra khong can DB/Tomcat: java -cp ... com.jlearn.PureCheck */
public class PureCheck {
    static int pass, fail;
    static void check(String n, boolean c) { if (c) pass++; else { fail++; System.out.println("FAIL: " + n); } }

    public static void main(String[] a) {
        // open redirect
        check("noi bo hop le", "/my-courses?x=1".equals(SafeRedirect.internalPath("/my-courses?x=1", "/d")));
        String[] bad = {"//evil.com", "https://evil.com", "http://evil.com/x", "/\\evil.com", "evil.com", "javascript:alert(1)", "/a\r\nSet-Cookie: x=1",
                "/login", "/register?x=1", "/logout", "///evil.com", "/%2f%2fevil.com", "", null, "/x://evil", "\\\\evil.com"};
        for (String b : bad) check("chan: " + b, "/d".equals(SafeRedirect.internalPath(b, "/d")));

        // BCrypt: hash moi + hash seed PHP ($2y$)
        String h = PasswordHasher.hash("Abcdef12");
        check("hash dung dinh dang", h.startsWith("$2a$"));
        check("verify dung", PasswordHasher.verify("Abcdef12", h));
        check("verify sai", !PasswordHasher.verify("abcdef12", h));
        check("user khong ton tai -> false (van ton thoi gian)", !PasswordHasher.verify("x", null));
        String seed = "$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
        boolean pw123 = PasswordHasher.verify("password123", seed), pw = PasswordHasher.verify("password", seed);
        System.out.println("seed hash: password123=" + pw123 + ", password=" + pw);

        // validator
        Validator v = new Validator().username("u", "ab").email("e", "a@b").newPassword("p", "abcdefgh").newPassword("p2", "12345678")
                .newPassword("p3", "Abcdef12");
        check("username ngan", v.errors().containsKey("u"));
        check("email xau", v.errors().containsKey("e"));
        check("mat khau chi chu", v.errors().containsKey("p"));
        check("mat khau chi so", v.errors().containsKey("p2"));
        check("mat khau tot", !v.errors().containsKey("p3"));
        check("username ok", new Validator().username("u", "Nguyen_An99").ok());
        check("username co khoang trang", !new Validator().username("u", "a b c").ok());
        check("username co ky tu dac biet", !new Validator().username("u", "a<script>").ok());
        check("mat khau > 72 byte", !new Validator().newPassword("p", "A1" + new String(new char[80]).replace('\0', 'x')).ok());
        check("mat khau trung ten dang nhap", !new Validator().newPassword("p", "Nguyen99x", "nguyen99x").ok());
        check("ten hien thi rong", !new Validator().displayName("n", "   ").ok());
        check("ten hien thi ky tu dieu khien", !new Validator().displayName("n", "a\u0000b").ok());

        // pager
        Pager p = Pager.of(1, 20, 45);
        check("45 dong -> 3 trang", p.getTotalPages() == 3 && p.getOffset() == 0 && !p.isHasPrev() && p.isHasNext());
        check("trang vuot -> trang cuoi", Pager.of(99, 20, 45).getPage() == 3);
        check("trang 0 -> 1", Pager.of(0, 20, 45).getPage() == 1);
        check("0 dong -> 1 trang", Pager.of(1, 20, 0).getTotalPages() == 1);
        check("cua so 5 trang", Pager.of(10, 20, 400).getWindow().size() == 5);
        System.out.println("PASS=" + pass + " FAIL=" + fail);
    }
}
