package jim;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlumeIdWiper  implements Interceptor{
    static Logger log = LoggerFactory.getLogger(FlumeIdWiper.class);

    public static class Builder implements Interceptor.Builder {

        @Override public Interceptor build() {
            log.info("Wiper Builder");
            return new FlumeIdWiper();
        }

        @Override public void configure(Context context) {
            log.info("Wiper Builder configure {}", context);
        }
    }

    @Override public void initialize() {
        log.info("INITIALIZING");
    }

    Pattern pattern = Pattern.compile("(Bearer|Basic) ([\\w-]{8})([\\w-]*)", Pattern.MULTILINE);

    @Override public Event intercept(Event event) {
        String body = new String(event.getBody());
        log.info("Processing event {}", body);
        Matcher match = pattern.matcher(body);
        if (match.find()) {
            String newBody = match.replaceAll("$1 $2********");
            event.setBody(newBody.getBytes());
        }
        return event;
    }

    @Override public List<Event> intercept(List<Event> events) {
        log.info("Logging events {}", events.size());
        return ImmutableList.copyOf(Lists.transform(events, new Function<Event, Event>() {
            @Override public Event apply(Event input) {return intercept(input);}
        }));
    }

    @Override public void close() {
        log.info("CLOSING");
    }
}
