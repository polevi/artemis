import org.junit.Test;

import com.mycompany.app.helpers.SwiftMTHelper;

import static org.junit.Assert.assertEquals;

import java.util.Random;

public class SwiftMTHelperTest {

    @Test
    public void testParseMessageId() {
        Random random = new Random();
        long id = random.nextInt(100);
        long result = SwiftMTHelper.parseMessageId(SwiftMTHelper.createMT103(id));
        assertEquals(id, result);
    }
}
