<?php

return [
    // Nơi hiển thị ảnh món. Ảnh lưu trong DB dưới dạng tên file (vd: food_2026..._ab12.jpg).
    // - Mặc định: '/uploads/foods/' -> phục vụ bởi chính web admin (php -S localhost:8000).
    // - Muốn xem ảnh do APP mobile upload (nằm trong appGoiMon_API/uploads/foods), đổi thành
    //   URL của API đang chạy, vd: 'http://localhost:8080/uploads/foods/'.
    'image_base' => '/uploads/foods/',

    // Thư mục lưu ảnh khi upload từ web admin (đường dẫn tuyệt đối trên đĩa).
    // Để trống => dùng <web_admin>/uploads/foods.
    'upload_dir' => '',
];
