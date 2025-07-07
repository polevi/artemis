import org.junit.Test;

import com.mycompany.app.helpers.SwiftMTHelper;

import static org.junit.Assert.assertEquals;

import java.util.Random;

public class SwiftMTHelperTest {

    SwiftMTHelper helper = new SwiftMTHelper();

    @Test
    public void testParseMessageId() {
        Random random = new Random();
        int id = random.nextInt(100);
        int result = SwiftMTHelper.parseMessageId(SwiftMTHelper.createMT103(id));
        assertEquals(id, result);
    }
}
