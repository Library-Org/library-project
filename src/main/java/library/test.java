package library;



import library.services.NotificationService;
import library.config.EmailConfig;
import library.models.User;

/**
 * تجربة مباشرة لإرسال إيميل
 */
public class test {
    
    public static void main(String[] args) {
        System.out.println("🚀  tبدء تجربة إرسال الإيميل...");
        
        // 1. إعداد配置 الإيميل (استبدل بالبيانات الحقيقية)
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setHost("smtp.gmail.com"); // أو smtp.outlook.com لـ Outlook
        emailConfig.setPort("587");
        emailConfig.setUsername("ramadafer25@gmail.com"); // 🔹 ضع ايميلك هنا
        emailConfig.setPassword("pbfu okca ivpa wkft"); // 🔹 ضع كلمة المرور التطبيقية هنا
        emailConfig.setEnableTLS(true);
        
        // 2. إنشاء خدمة الإشعارات
        NotificationService notificationService = new NotificationService(emailConfig, true);
        
        // 3. إيميلك الشخصي للتجربة
        String yourPersonalEmail = "ramadafer25@gmail.com"; // 🔹 ضع ايميلك هنا
        
        try {
            System.out.println("📧 جارٍ إرسال إيميل تجريبي...");
            
            // تجربة 1: إرسال إيميل بسيط
            boolean success = notificationService.sendEmail(
                yourPersonalEmail,
                "🔔 تجربة نظام المكتبة - Test from Library System",
                "مرحبا!\n\n" +
                "هذا إيميل تجريبي من نظام إدارة المكتبة.\n" +
                "إذا استلمت هذا الإيميل، فهذا يعني أن النظام يعمل بشكل صحيح! 🎉\n\n" +
                "مع أطيب التحيات،\nنظام إدارة المكتبة"
            );
            
            if (success) {
                System.out.println("✅ تم إرسال الإيميل بنجاح إلى: " + yourPersonalEmail);
                System.out.println("🎉 النظام يعمل بشكل ممتاز!");
            } else {
                System.out.println("❌ فشل إرسال الإيميل");
                System.out.println("💡 تحقق من:");
                System.out.println("   - بيانات الإيميل وكلمة المرور");
                System.out.println("   - تفعيل SMTP في حسابك");
                System.out.println("   - كلمة المرور التطبيقية (لجيميل)");
            }
            
        } catch (Exception e) {
            System.err.println("❌ حدث خطأ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}