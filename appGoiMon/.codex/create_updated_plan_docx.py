from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile
from html import escape


OUT = Path(
    r"D:\nam2\Ki_2\Lap_Trinh_Di_Dong"
    r"\ke_hoach_trien_khai_app_goi_mon_buffet_cap_nhat_2026-06-12_fixed.docx"
)


def run_text(text: str) -> str:
    return f'<w:r><w:t xml:space="preserve">{escape(text)}</w:t></w:r>'


def para(text: str = "", style: str | None = None) -> str:
    ppr = f'<w:pPr><w:pStyle w:val="{style}"/></w:pPr>' if style else ""
    return f"<w:p>{ppr}{run_text(text)}</w:p>"


def bullet(text: str, level: int = 0) -> str:
    indent = 360 + level * 360
    return (
        "<w:p>"
        f'<w:pPr><w:pStyle w:val="ListParagraph"/><w:ind w:left="{indent}" w:hanging="360"/></w:pPr>'
        f'{run_text("- " + text)}'
        "</w:p>"
    )


def code_block(text: str) -> str:
    return "".join(
        f'<w:p><w:pPr><w:pStyle w:val="Code"/></w:pPr>{run_text(line)}</w:p>'
        for line in text.split("\n")
    )


def table(rows: list[list[str]]) -> str:
    xml = [
        '<w:tbl><w:tblPr><w:tblStyle w:val="TableGrid"/><w:tblW w:w="0" w:type="auto"/>'
        '<w:tblBorders><w:top w:val="single" w:sz="4" w:space="0" w:color="auto"/>'
        '<w:left w:val="single" w:sz="4" w:space="0" w:color="auto"/>'
        '<w:bottom w:val="single" w:sz="4" w:space="0" w:color="auto"/>'
        '<w:right w:val="single" w:sz="4" w:space="0" w:color="auto"/>'
        '<w:insideH w:val="single" w:sz="4" w:space="0" w:color="auto"/>'
        '<w:insideV w:val="single" w:sz="4" w:space="0" w:color="auto"/>'
        "</w:tblBorders></w:tblPr>"
    ]
    for row in rows:
        xml.append("<w:tr>")
        for cell in row:
            xml.append('<w:tc><w:tcPr><w:tcW w:w="3000" w:type="dxa"/></w:tcPr>')
            xml.append(para(cell))
            xml.append("</w:tc>")
        xml.append("</w:tr>")
    xml.append("</w:tbl>")
    return "".join(xml)


parts: list[str] = []
parts.append(para("KẾ HOẠCH TRIỂN KHAI APP GỌI MÓN BUFFET", "Title"))
parts.append(para("Bản cập nhật theo luồng app hiện tại", "Subtitle"))
parts.append(para("Ngày cập nhật: 12/06/2026"))
parts.append(para("Phạm vi: Android Jetpack Compose + PHP API + MySQL buffet_ordering"))

parts.append(para("1. Mục tiêu tài liệu", "Heading1"))
parts.append(
    para(
        "Tài liệu này thay thế kế hoạch cũ vì luồng xây dựng app hiện tại đã thay đổi đáng kể. "
        "Bản mới mô tả đúng kiến trúc đang có trong source code, các luồng user/admin đang chạy, "
        "API backend hiện có và các tính năng cần triển khai tiếp."
    )
)
parts.append(bullet("Làm tài liệu định hướng cho phần báo cáo môn Lập Trình Di Động."))
parts.append(bullet("Giúp tiếp tục phát triển app theo đúng trạng thái hiện tại, tránh quay lại luồng cũ."))
parts.append(bullet("Tách rõ phần đã xây dựng, phần cần kiểm thử thủ công và phần cần phát triển thêm."))

parts.append(para("2. Hiện trạng app hiện tại", "Heading1"))
parts.append(
    table(
        [
            ["Khu vực", "Hiện trạng"],
            ["Android", "Jetpack Compose, Retrofit, ViewModel, StateFlow. MainActivity route theo role user/admin."],
            ["Backend", "PHP API chạy trong C:/xampp/htdocs/appOrder/appGoiMon_API."],
            ["Database", "MySQL database buffet_ordering, dùng các bảng tables, sessions, combos, menu, orders."],
            ["User flow", "Đã có nhập bàn, chọn combo, nhập số khách, tạo phiên, chờ admin xác nhận, xem menu theo combo."],
            ["Admin flow", "Đã có dashboard, quản lý bàn, xác nhận thanh toán, đóng bàn, xử lý món, quản lý menu và danh mục."],
        ]
    )
)

parts.append(para("3. Kiến trúc tổng quan", "Heading1"))
parts.append(
    code_block(
        """Android app
  |-- MainActivity route theo role
  |-- UI Compose screens
  |-- ViewModel giữ state màn hình
  |-- Repository gọi Retrofit
  v
PHP API appGoiMon_API
  |-- api/auth
  |-- api/user
  |-- api/admin
  |-- config/helpers.php, database.php
  v
MySQL buffet_ordering"""
    )
)

parts.append(para("4. Luồng tổng thể", "Heading1"))
parts.append(
    code_block(
        """Đăng nhập
  |-- role = user
  |     |-- Nhập mã bàn
  |     |-- Kiểm tra trạng thái bàn
  |     |-- Bàn trống: chọn combo + số khách + phương thức thanh toán
  |     |-- Tạo phiên pending_payment
  |     |-- Chờ admin xác nhận
  |     |-- Phiên active: xem menu theo combo
  |
  |-- role = admin
        |-- Dashboard thống kê
        |-- Quản lý bàn và phiên bàn
        |-- Xác nhận thanh toán / đóng bàn
        |-- Quản lý món chờ xử lý
        |-- Quản lý menu và danh mục"""
    )
)

parts.append(para("5. Luồng user hiện tại", "Heading1"))
sections = [
    (
        "5.1 Nhập và kiểm tra bàn",
        [
            "Màn hình: SelectTableScreen.",
            "ViewModel: OrderViewModel.checkTable().",
            "API: POST api/user/check_table.php.",
            "Nếu bàn không tồn tại, app giữ user ở màn nhập bàn và hiển thị lỗi.",
            "Nếu bàn trống, app chuyển sang COMBO_SETUP.",
            "Nếu bàn có session pending_payment, app chuyển sang WAITING_PAYMENT.",
            "Nếu bàn có session active, app chuyển sang ACTIVE_MENU và load menu theo combo_id.",
        ],
    ),
    (
        "5.2 Chọn combo và số khách",
        [
            "Màn hình: ComboAndGuestScreen.",
            "API load combo: GET api/user/get_combos.php.",
            "User chọn combo, nhập số khách tính tiền, trẻ em miễn phí và phương thức cash/qr.",
            "Tạm tính = paid_guest_count * price_per_person.",
            "Validation hiện có: khách trả phí phải lớn hơn 0; trẻ em miễn phí không âm.",
        ],
    ),
    (
        "5.3 Tạo phiên và chờ thanh toán",
        [
            "API tạo phiên: POST api/user/create_session.php.",
            "Sau khi tạo phiên, trạng thái là pending_payment.",
            "Màn hình WaitingPaymentScreen cho phép refresh trạng thái.",
            "API refresh: GET api/user/get_session_status.php.",
            "Khi admin xác nhận thanh toán, session chuyển active và user vào menu.",
        ],
    ),
    (
        "5.4 Xem menu theo combo",
        [
            "Màn hình: TableOrderScreen.",
            "API: GET api/user/get_menu_by_combo.php?combo_id=...",
            "Chỉ hiển thị món status available thuộc combo đang dùng.",
            "Ảnh món được resolve từ BASE_URL + uploads/foods hoặc URL tuyệt đối.",
            "Hiện tại app mới dừng ở xem menu, chưa có giỏ món và gửi order từ user.",
        ],
    ),
]
for title, bullets in sections:
    parts.append(para(title, "Heading2"))
    for item in bullets:
        parts.append(bullet(item))

parts.append(para("6. Luồng admin hiện tại", "Heading1"))
admin_sections = [
    (
        "6.1 Admin shell",
        [
            "Màn hình: AdminDashboardScreen.",
            "Điều hướng bằng bottom navigation gồm Dashboard, Bàn, Đơn, Menu.",
            "Admin đăng xuất quay về login và reset user flow.",
        ],
    ),
    (
        "6.2 Dashboard",
        [
            "API: GET api/admin/get_dashboard_stats.php.",
            "Hiển thị doanh thu tổng, doanh thu hôm nay, số bàn trống/đang dùng/chờ thanh toán, số món theo trạng thái, số danh mục active.",
            "Doanh thu tính theo table_sessions.payment_status = paid; doanh thu hôm nay dùng ngày server MySQL.",
        ],
    ),
    (
        "6.3 Quản lý bàn",
        [
            "API danh sách bàn: GET api/admin/get_tables.php.",
            "API chi tiết phiên bàn: GET api/admin/get_table_session.php.",
            "Admin có thể xác nhận thanh toán bằng POST api/admin/confirm_payment.php.",
            "Admin có thể đóng bàn active bằng POST api/admin/close_table.php.",
        ],
    ),
    (
        "6.4 Quản lý đơn gọi món",
        [
            "Màn hình: ManageOrderScreen.",
            "API: GET api/admin/get_pending_orders.php.",
            "Admin có thể duyệt, từ chối hoặc đánh dấu đã phục vụ qua approve_order_item.php, reject_order_item.php, mark_item_served.php.",
            "Hiện user chưa có UI gửi order nên màn này chủ yếu sẵn sàng cho bước tiếp theo.",
        ],
    ),
    (
        "6.5 Quản lý menu và danh mục",
        [
            "Màn hình: ManageFoodScreen và ManageCategoryScreen.",
            "API menu: get_menu_items.php, manage_menu_item.php.",
            "API danh mục: get_categories.php, manage_category.php.",
            "Menu item hỗ trợ create/update/delete mềm/status available/out_of_stock/hidden.",
            "Category hỗ trợ create/update/delete mềm/status active/inactive.",
        ],
    ),
]
for title, bullets in admin_sections:
    parts.append(para(title, "Heading2"))
    for item in bullets:
        parts.append(bullet(item))

parts.append(para("7. API hiện có", "Heading1"))
parts.append(
    table(
        [
            ["Nhóm", "Endpoint chính", "Mục đích"],
            ["auth", "api/auth/login.php, register.php", "Đăng nhập/đăng ký user và admin theo role."],
            ["user", "check_table.php", "Kiểm tra bàn và session đang mở."],
            ["user", "get_combos.php", "Lấy combo active."],
            ["user", "create_session.php", "Tạo phiên pending_payment."],
            ["user", "get_session_status.php", "Refresh trạng thái phiên."],
            ["user", "get_menu_by_combo.php", "Lấy menu theo combo active."],
            ["user", "create_order.php", "Backend đã có, Android chưa tích hợp UI gửi order."],
            ["user", "get_order_status.php", "Backend đã có, Android chưa có màn theo dõi order."],
            ["user", "request_checkout.php", "Backend đã có, Android chưa có nút yêu cầu thanh toán."],
            ["admin", "get_dashboard_stats.php", "Dashboard doanh thu và vận hành."],
            ["admin", "get_tables.php, get_table_session.php", "Danh sách bàn và chi tiết phiên."],
            ["admin", "confirm_payment.php, close_table.php", "Xác nhận thanh toán và đóng bàn."],
            ["admin", "get_pending_orders.php + action endpoints", "Xử lý món pending."],
            ["admin", "get_menu_items.php, manage_menu_item.php", "Quản lý món."],
            ["admin", "get_categories.php, manage_category.php", "Quản lý danh mục."],
            ["admin", "manage_combo.php", "Backend có sẵn một phần, Android chưa có UI quản lý combo."],
        ]
    )
)

parts.append(para("8. Những phần đã hoàn thành", "Heading1"))
for item in [
    "Hoàn thiện route chính theo role user/admin trong MainActivity.",
    "Hoàn thiện user session flow đến bước xem menu theo combo.",
    "Hiển thị ảnh món trên TableOrderScreen và fallback khi thiếu/lỗi ảnh.",
    "Hoàn thiện admin shell mobile-first với bottom navigation.",
    "Hoàn thiện dashboard admin dùng số liệu thật từ MySQL.",
    "Hoàn thiện quản lý bàn: xem phiên, xác nhận thanh toán, đóng bàn.",
    "Hoàn thiện quản lý món pending phía admin.",
    "Hoàn thiện quản lý menu item và category.",
    "Đã chạy compile Kotlin trong quá trình phát triển.",
]:
    parts.append(bullet(item))

parts.append(para("9. Tính năng cần xây dựng thêm", "Heading1"))
priority_sections = [
    (
        "Ưu tiên P1 - cần làm để app gọi món hoàn chỉnh",
        [
            "Thêm giỏ món phía user: chọn món, tăng/giảm số lượng, ghi chú từng món.",
            "Tích hợp POST api/user/create_order.php để gửi đơn gọi món từ TableOrderScreen.",
            "Thêm màn hoặc khu vực theo dõi trạng thái món đã gọi bằng GET api/user/get_order_status.php.",
            "Thêm nút yêu cầu thanh toán/kết thúc bữa bằng POST api/user/request_checkout.php.",
            "Kiểm thử thủ công toàn bộ flow user: bàn không tồn tại, bàn trống, pending_payment, active, menu theo combo.",
            "Kiểm thử thủ công toàn bộ flow admin: login, dashboard, xác nhận thanh toán, đóng bàn, order actions, CRUD menu/category.",
        ],
    ),
    (
        "Ưu tiên P2 - cải thiện quản trị và vận hành",
        [
            "Xây UI quản lý combo cho admin: tạo/sửa combo, giá, mô tả, trạng thái, gán món vào combo.",
            "Thêm upload/chọn ảnh món thay vì nhập đường dẫn ảnh thủ công.",
            "Lưu phiên đăng nhập local để app không mất login khi restart.",
            "Tách cấu hình BASE_URL theo môi trường: emulator dùng 10.0.2.2, điện thoại thật dùng IP LAN, production dùng domain.",
            "Dọn tiếng Việt mojibake trong một số message PHP cũ.",
            "Thêm search/filter cho menu, category, orders và tables.",
        ],
    ),
    (
        "Ưu tiên P3 - hoàn thiện chất lượng sản phẩm",
        [
            "Bổ sung xác thực token/session thay vì chỉ dựa vào role trả về từ login.",
            "Thêm báo cáo doanh thu theo ngày/tháng/combo/món bán chạy.",
            "Thêm thông báo realtime hoặc polling định kỳ cho order/admin.",
            "Viết test ViewModel/repository cho các luồng chính.",
            "Chuẩn hóa UI text tiếng Việt có dấu trên Android.",
        ],
    ),
]
for title, bullets in priority_sections:
    parts.append(para(title, "Heading2"))
    for item in bullets:
        parts.append(bullet(item))

parts.append(para("10. Kế hoạch triển khai tiếp theo", "Heading1"))
parts.append(
    table(
        [
            ["Giai đoạn", "Mục tiêu", "Công việc chính"],
            ["GĐ1", "Hoàn chỉnh user order", "Giỏ món, create_order, trạng thái món, request_checkout."],
            ["GĐ2", "Hoàn chỉnh admin combo/media", "Quản lý combo, gán món vào combo, upload ảnh món."],
            ["GĐ3", "Ổn định vận hành", "Lưu login, cấu hình môi trường, sửa encoding, filter/search."],
            ["GĐ4", "Kiểm thử và báo cáo", "Manual test, compile/lint, ảnh màn hình, tài liệu báo cáo."],
        ]
    )
)

parts.append(para("11. Checklist kiểm thử", "Heading1"))
for item in [
    "Login user đúng role và chuyển vào màn nhập bàn.",
    "Nhập bàn không tồn tại hiển thị lỗi dễ hiểu.",
    "Bàn trống chuyển sang chọn combo và nhập số khách.",
    "Tạo session pending_payment thành công.",
    "Admin thấy bàn waiting_payment và xác nhận thanh toán.",
    "User refresh thấy session active và menu hiển thị đúng combo, có ảnh món.",
    "Admin dashboard load số liệu doanh thu và trạng thái bàn.",
    "Admin duyệt/từ chối/đánh dấu đã phục vụ món pending.",
    "Admin CRUD menu item và category.",
    "Đóng bàn active đưa bàn về available.",
]:
    parts.append(bullet(item))

parts.append(para("12. Rủi ro và lưu ý kỹ thuật", "Heading1"))
for item in [
    "BASE_URL hiện đang phụ thuộc IP LAN. Khi đổi mạng, app có thể không gọi được API/ảnh.",
    "Một số endpoint PHP cũ còn message bị lỗi encoding; nên sửa trước khi nộp báo cáo hoặc demo.",
    "Backend đã có create_order/get_order_status/request_checkout nhưng Android chưa nối UI, đây là khoảng trống lớn nhất của user flow.",
    "Quản lý combo hiện chưa có UI Android, trong khi menu user phụ thuộc combo_menu_items.",
    "Cần kiểm thử trên thiết bị thật hoặc emulator phone-size vì UI được thiết kế mobile-first.",
]:
    parts.append(bullet(item))

parts.append(para("13. File và module tham chiếu", "Heading1"))
for item in [
    "Android root: C:/xampp/htdocs/appOrder/appGoiMon",
    "Backend root: C:/xampp/htdocs/appOrder/appGoiMon_API",
    "Main routing: app/src/main/java/com/example/appgoimon/MainActivity.kt",
    "User ViewModel: app/src/main/java/com/example/appgoimon/viewmodel/OrderViewModel.kt",
    "User screens: SelectTableScreen, ComboAndGuestScreen, WaitingPaymentScreen, TableOrderScreen",
    "Admin shell: AdminDashboardScreen.kt",
    "Admin screens: ManageTableScreen, ManageOrderScreen, ManageFoodScreen, ManageCategoryScreen",
    "API contract: app/src/main/java/com/example/appgoimon/data/remote/ApiService.kt",
    "OpenSpec changes: user-combo-session-flow, admin-interface",
]:
    parts.append(bullet(item))

body = "".join(parts)

document_xml = f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <w:body>
    {body}
    <w:sectPr><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="708" w:footer="708" w:gutter="0"/></w:sectPr>
  </w:body>
</w:document>'''

styles_xml = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal"><w:name w:val="Normal"/><w:qFormat/><w:rPr><w:sz w:val="24"/><w:szCs w:val="24"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Title"><w:name w:val="Title"/><w:qFormat/><w:pPr><w:jc w:val="center"/><w:spacing w:after="240"/></w:pPr><w:rPr><w:b/><w:sz w:val="36"/><w:szCs w:val="36"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Subtitle"><w:name w:val="Subtitle"/><w:qFormat/><w:pPr><w:jc w:val="center"/><w:spacing w:after="240"/></w:pPr><w:rPr><w:i/><w:color w:val="666666"/><w:sz w:val="26"/><w:szCs w:val="26"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Heading1"><w:name w:val="heading 1"/><w:qFormat/><w:pPr><w:spacing w:before="360" w:after="120"/></w:pPr><w:rPr><w:b/><w:color w:val="7A3E00"/><w:sz w:val="30"/><w:szCs w:val="30"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Heading2"><w:name w:val="heading 2"/><w:qFormat/><w:pPr><w:spacing w:before="240" w:after="80"/></w:pPr><w:rPr><w:b/><w:color w:val="9A5A00"/><w:sz w:val="26"/><w:szCs w:val="26"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="ListParagraph"><w:name w:val="List Paragraph"/><w:pPr><w:spacing w:after="80"/></w:pPr><w:rPr><w:sz w:val="24"/><w:szCs w:val="24"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Code"><w:name w:val="Code"/><w:pPr><w:spacing w:after="0"/></w:pPr><w:rPr><w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/><w:sz w:val="20"/><w:szCs w:val="20"/></w:rPr></w:style>
  <w:style w:type="table" w:styleId="TableGrid"><w:name w:val="Table Grid"/><w:tblPr><w:tblBorders><w:top w:val="single" w:sz="4" w:space="0" w:color="auto"/><w:left w:val="single" w:sz="4" w:space="0" w:color="auto"/><w:bottom w:val="single" w:sz="4" w:space="0" w:color="auto"/><w:right w:val="single" w:sz="4" w:space="0" w:color="auto"/><w:insideH w:val="single" w:sz="4" w:space="0" w:color="auto"/><w:insideV w:val="single" w:sz="4" w:space="0" w:color="auto"/></w:tblBorders></w:tblPr></w:style>
</w:styles>'''

content_types = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>'''

rels = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>'''

doc_rels = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>'''

core = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dcmitype="http://purl.org/dc/dcmitype/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:title>Kế hoạch triển khai app gọi món buffet cập nhật</dc:title>
  <dc:creator>Codex</dc:creator>
  <cp:lastModifiedBy>Codex</cp:lastModifiedBy>
</cp:coreProperties>'''

app = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties" xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
  <Application>Codex</Application>
</Properties>'''

OUT.parent.mkdir(parents=True, exist_ok=True)
with ZipFile(OUT, "w", ZIP_DEFLATED) as z:
    z.writestr("[Content_Types].xml", content_types.encode("utf-8"))
    z.writestr("_rels/.rels", rels.encode("utf-8"))
    z.writestr("word/_rels/document.xml.rels", doc_rels.encode("utf-8"))
    z.writestr("word/document.xml", document_xml.encode("utf-8"))
    z.writestr("word/styles.xml", styles_xml.encode("utf-8"))
    z.writestr("docProps/core.xml", core.encode("utf-8"))
    z.writestr("docProps/app.xml", app.encode("utf-8"))

print(OUT)
