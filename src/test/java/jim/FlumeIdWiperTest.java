package jim;

import com.google.common.collect.ImmutableList;
import org.apache.flume.Event;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class FlumeIdWiperTest {

    @Test
    public void wipeIds() {

        String test =
            "Authorization: Bearer 1234xxxx-abcdef\n" +
            "Other stuff\n" +
            "Authorization: Basic ABCDEFGHIJKLMNOP123456789\n";

        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getBody()).thenReturn(test.getBytes());

        new FlumeIdWiper().intercept(ImmutableList.of(event));

        String expected = "Authorization: Bearer 1234xxxx********\n" +
                        "Other stuff\n" +
                        "Authorization: Basic ABCDEFGH********\n";

        Mockito.verify(event).setBody(Matchers.eq(expected.getBytes()));
    }
}
