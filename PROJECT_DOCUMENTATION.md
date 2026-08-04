# TÀI LIỆU CHI TIẾT DỰ ÁN: AI STUDY MENTOR

## 1. Giới thiệu tổng quan
**AI Study Mentor** là một ứng dụng di động trên nền tảng Android, đóng vai trò như một người gia sư thông minh (AI Mentor) đồng hành cùng học sinh, sinh viên trong quá trình học tập. Ứng dụng giúp người dùng lập kế hoạch, theo dõi tiến độ các môn học và đặc biệt là có khả năng giải đáp các thắc mắc, giải bài tập trực tiếp thông qua trí tuệ nhân tạo (Generative AI) bằng cả văn bản và hình ảnh.

---

## 2. Các công nghệ sử dụng (Technology Stack)

### 2.1. Nền tảng & Ngôn ngữ
- **Hệ điều hành mục tiêu:** Android
- **Ngôn ngữ lập trình:** Java
- **Môi trường phát triển:** Android Studio, Gradle

### 2.2. Giao diện người dùng (UI/UX)
- **XML Layouts:** Xây dựng giao diện tĩnh và động.
- **Material Design 3:** Sử dụng các component hiện đại của Google như `MaterialCardView`, `BottomNavigationView`, `LinearProgressIndicator` để mang lại trải nghiệm mượt mà, đồng nhất.
- **RecyclerView:** Xử lý hiển thị danh sách động (Lịch sử chat, Lộ trình học tập, Tiến độ môn học, Hoạt động gần đây).
- **Jetpack Navigation Component:** Quản lý điều hướng (routing) toàn bộ ứng dụng qua `nav_graph.xml`, hỗ trợ quản lý backstack và chuyển cảnh an toàn (safe args).

### 2.3. Lưu trữ & Quản lý dữ liệu (Local Storage)
- **SQLite Database:** Xây dựng cơ sở dữ liệu nội bộ (Local Database) hoàn chỉnh với kiến trúc Repository Pattern (`UserRepository`, `ActivityRepository`) để lưu trữ:
  - Thông tin người dùng (Email, Tên, XP, Level, Password).
  - Lịch sử hoạt động (Recent Activities).
  - Tiến độ môn học (Subject Progress).
  - Lộ trình học tập (Study Plans).
- **SharedPreferences:** Lưu trữ trạng thái phiên đăng nhập (session), thông tin cấu hình và dữ liệu bộ nhớ tạm (`currentUserEmail`, `isLoggedIn`).

### 2.4. Tích hợp Trí tuệ nhân tạo (AI & API Networking)
- **Google Gemini API (gemini-3.5-flash-lite):** Lõi xử lý trí tuệ nhân tạo của ứng dụng, hỗ trợ xử lý ngôn ngữ tự nhiên (NLP) và thị giác máy tính (Vision). Nhận diện và giải bài tập qua văn bản hoặc phân tích hình ảnh chụp từ camera.
- **Retrofit2 & OkHttp3:** Xử lý các request HTTP RESTful API gửi đến máy chủ của Google Gemini.
- **Android Camera API (ActivityResultLauncher):** Kích hoạt máy ảnh, chụp và xử lý ảnh bitmap, sau đó mã hóa Base64 gửi lên AI.

---

## 3. Kiến trúc hệ thống
Ứng dụng áp dụng kiến trúc hướng Module theo Fragment (Single-Activity Architecture). `MainActivity` chỉ đóng vai trò Host, trong khi mọi luồng nghiệp vụ được chia nhỏ thành các Fragment:
- Cụm Xác thực (Auth): `LoginFragment`, `RegisterFragment`, `OtpFragment`, `AuthWelcomeFragment`.
- Cụm Khởi tạo (Onboarding): `OnboardingHostFragment`, `OnboardingSetupFragment`.
- Cụm Chức năng chính: `DashboardFragment`, `PathFragment` (Lộ trình), `ProgressFragment` (Tiến độ), `ProfileFragment` (Hồ sơ), `AskFragment` (Hỏi đáp AI).

---

## 4. Các tính năng cốt lõi (Core Features)

### 4.1. Onboarding & Cá nhân hóa
- **Giao diện chào mừng:** Người dùng mới hoặc người chưa đăng nhập sẽ được đưa vào luồng Onboarding.
- **Chọn môn học mục tiêu:** Cho phép người dùng tick chọn các môn học muốn tập trung (Toán, Lý, Hóa, Sinh, Anh...). Dữ liệu này được lưu lại và tự động đồng bộ vào Database sau khi đăng ký thành công.

### 4.2. Hệ thống Xác thực (Authentication)
- Đăng ký tài khoản với tính năng mô phỏng gửi mã xác nhận OTP.
- Đăng nhập an toàn, duy trì phiên đăng nhập và định danh qua Email (`currentUserEmail`).
- Luồng Đăng xuất chuẩn hóa, tự động xóa sạch ngăn xếp màn hình (clear backstack) và đưa người dùng về lại màn hình Onboarding.

### 4.3. Bảng điều khiển (Dashboard)
- Hiển thị lời chào cá nhân hóa.
- Theo dõi thanh tiến độ Mục tiêu trong ngày (Daily Goal).
- Trích xuất danh sách các "Hoạt động gần đây" (Recent Activity) từ CSDL.
- Hiển thị Lộ trình gợi ý nhanh.

### 4.4. Gia sư AI Thông minh (AI Ask/Chat)
- **Chatbot thời gian thực:** Giao diện nhắn tin trực quan mô phỏng khung chat với bong bóng chat (chat bubbles).
- **Đa phương thức nhập liệu:**
  - Nhập văn bản bình thường.
  - Chụp ảnh đề bài trực tiếp qua Camera: Ứng dụng tự động thu nhỏ ảnh, mã hóa và gửi yêu cầu giải bài tập bằng hình ảnh tới Gemini API.
- **Prompt Engineering:** Hệ thống được lập trình sẵn các System Prompts ngầm định để ép AI đóng vai "Gia sư", trả lời ngắn gọn, súc tích và tránh sử dụng các ký tự toán học phức tạp (như LaTeX) để đảm bảo hiển thị chuẩn xác trên giao diện di động.

### 4.5. Quản lý Lộ trình & Tiến độ Học tập
- **Trang Tiến độ (Progress):** Giao diện linh động (Dynamic UI). Tự động lấy danh sách các môn học người dùng đã chọn từ lúc Onboarding trong CSDL để vẽ ra các thẻ (Card) tiến độ phần trăm hoàn thành. 
- **Trang Lộ trình (Path):** Phân loại các kế hoạch học tập thành 3 luồng: Đang học (Continue), Kế hoạch sắp tới (Plan Item) và Đề xuất (Suggestion).

### 4.6. Hồ sơ Cá nhân (Profile) & Gamification
- Hiển thị Tên người dùng hiện tại.
- **Hệ thống Gamification:** Đo lường động lực học qua Điểm kinh nghiệm (XP) và Cấp độ (Level). Trình diễn thanh tiến trình tới cấp độ tiếp theo.
- **Huy hiệu Thành tích:** Các thẻ huy hiệu (Ví dụ: "Kiên trì" với icon bản đồ lộ trình) để vinh danh người dùng.
- **Cài đặt hệ thống:** Khu vực cấu hình ứng dụng bao gồm Quản lý Thông báo, Bảo mật, Trợ giúp & Hỗ trợ.

---

## 5. Điểm nhấn dùng để báo cáo / thuyết trình (Highlights)
- **Tích hợp API thế hệ mới:** Tích hợp mô hình `gemini-3.5-flash-lite` mang lại tốc độ phản hồi cực nhanh, khả năng hiểu ngôn ngữ tiếng Việt tốt và khả năng đọc hiểu hình ảnh sắc nét.
- **UX/UI mượt mà, đồng nhất:** Sử dụng Single-Activity Architecture và Navigation Component giúp chuyển trang cực kỳ trơn tru. Giao diện được thiết kế bo góc, phối màu Material hiện đại.
- **Xử lý Dữ liệu Nội bộ Thông minh:** Luồng dữ liệu chạy mượt từ lúc người dùng chỉ là "Khách" (chọn môn học lưu vào SharedPreferences) cho đến lúc trở thành "Thành viên" (đổ dữ liệu vào SQLite) mà không làm gián đoạn trải nghiệm.
- **Bảo mật và Tối ưu:** Mã hóa và nén hình ảnh (scale down bitmap) trước khi gửi lên AI nhằm tiết kiệm băng thông mạng và tránh quá tải bộ nhớ (OutOfMemory) cho thiết bị di động.
