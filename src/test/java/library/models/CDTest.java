package library.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import library.utils.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CDTest {

    private CD cd;

    @BeforeEach
    public void setUp() {
        cd = new CD("Test Title", "Test Artist", "Test Genre");
    }

    @Test
    public void testConstructorWithThreeParameters() {
        assertNotNull(cd);
        assertEquals("Test Title", cd.getTitle());
        assertEquals("Test Artist", cd.getArtist());
        assertEquals("Test Genre", cd.getGenre());
        assertEquals(0, cd.getTrackCount());  // Default value
        assertTrue(cd.isAvailable());  // Default value
    }

    @Test
    public void testConstructorWithFullParameters() {
        CD fullCd = new CD("Full Title", "Full Artist", "Full Genre", 10, "Test Publisher", 2020);
        assertNotNull(fullCd);
        assertEquals("Full Title", fullCd.getTitle());
        assertEquals(10, fullCd.getTrackCount());
        assertEquals("Test Publisher", fullCd.getPublisher());
        assertEquals(2020, fullCd.getReleaseYear());
        assertTrue(fullCd.isAvailable());
    }

    @Test
    public void testGettersAndSetters() {
        cd.setId("1");
        cd.setTitle("New Title");
        cd.setArtist("New Artist");
        cd.setGenre("New Genre");
        cd.setTrackCount(12);
        cd.setPublisher("New Publisher");
        cd.setReleaseYear(2022);
        cd.setAvailable(false);

        assertEquals("1", cd.getId());
        assertEquals("New Title", cd.getTitle());
        assertEquals("New Artist", cd.getArtist());
        assertEquals("New Genre", cd.getGenre());
        assertEquals(12, cd.getTrackCount());
        assertEquals("New Publisher", cd.getPublisher());
        assertEquals(2022, cd.getReleaseYear());
        assertFalse(cd.isAvailable());
    }

    @Test
    public void testCreatedAtAndUpdatedAt() {
        // التأكد من تعيين التاريخ عند الإنشاء
        assertNotNull(cd.getCreatedAt());
        assertNotNull(cd.getUpdatedAt());

        // التأكد من أن createdAt يتحول لوقت بشكل صحيح
        LocalDateTime createdAtDateTime = cd.getCreatedAtDateTime();
        assertNotNull(createdAtDateTime);
        assertEquals(cd.getCreatedAt(), DateUtils.toString(createdAtDateTime));

        // تحديث updatedAt
        cd.updateTimestamp();
        assertNotEquals(cd.getCreatedAt(), cd.getUpdatedAt());
    }

    @Test
    public void testUpdateTimestamp() {
        String initialUpdatedAt = cd.getUpdatedAt();

        // تأخير بسيط لتفادي الفروقات الدقيقة
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        cd.updateTimestamp();

        String updatedAt = cd.getUpdatedAt();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime initialDateTime = LocalDateTime.parse(initialUpdatedAt, formatter);
        LocalDateTime updatedDateTime = LocalDateTime.parse(updatedAt, formatter);

        // التأكد أن updatedAt أكبر من initialUpdatedAt
        assertTrue(updatedDateTime.isAfter(initialDateTime),
                "updatedAt should be after initial updatedAt");
    }
}
