import org.junit.Test;
import com.mycompany.app.swiftmt.SwiftMTHelper;

import static org.junit.Assert.assertEquals;

import java.util.Random;

public class SwiftMTHelperTest {

    SwiftMTHelper helper = new SwiftMTHelper();

    @Test
    public void testParseMessageId() {
        Random random = new Random();
        int id = random.nextInt(100);
        int result = helper.parseMessageId(helper.createMT103(id));
        assertEquals(id, result);
    }
}
