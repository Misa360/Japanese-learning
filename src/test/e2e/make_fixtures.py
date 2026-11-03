# Tao anh mau cho e2e.sh vao /tmp/e2e: ok.png (hop le), fake.png (ma PHP gia anh), poly.png (PNG + payload PHP),
# big.png (>2MB), gif.png (GIF doi duoi .png)
import os, struct, zlib
os.makedirs('/tmp/e2e', exist_ok=True)
def png(w, h):
    raw = b''.join(b'\x00' + bytes([200, 50, 50]) * w for _ in range(h))
    def ch(t, d):
        c = struct.pack('>I', len(d)) + t + d
        return c + struct.pack('>I', zlib.crc32(t + d) & 0xffffffff)
    return b'\x89PNG\r\n\x1a\n' + ch(b'IHDR', struct.pack('>IIBBBBB', w, h, 8, 2, 0, 0, 0)) + ch(b'IDAT', zlib.compress(raw)) + ch(b'IEND', b'')
open('/tmp/e2e/ok.png', 'wb').write(png(20, 20))
open('/tmp/e2e/fake.png', 'wb').write(b'<?php system($_GET["c"]); ?>' + b'A' * 100)
open('/tmp/e2e/poly.png', 'wb').write(png(20, 20) + b'<?php system("id"); ?>')
open('/tmp/e2e/big.png', 'wb').write(png(20, 20) + b'\0' * (3 * 1024 * 1024))
open('/tmp/e2e/gif.png', 'wb').write(b'GIF89a\x01\x00\x01\x00\x80\x00\x00\xff\xff\xff\x00\x00\x00!\xf9\x04\x01\x00\x00\x00\x00,\x00\x00\x00\x00\x01\x00\x01\x00\x00\x02\x02D\x01\x00;')
