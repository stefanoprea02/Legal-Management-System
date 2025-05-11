package ord.example.LegalManagementSystem.mappers;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StringToTimestampConverter implements Converter<String, Timestamp> {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";
    private final SimpleDateFormat dateFormat;

    public StringToTimestampConverter() {
        this.dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
    }

    @Override
    public Timestamp convert(@NonNull String source) {
        if (source.isEmpty()) {
            return null;
        }

        try {
            return new Timestamp(dateFormat.parse(source).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format is yyyy-MM-dd'T'HH:mm", e);
        }
    }
}
