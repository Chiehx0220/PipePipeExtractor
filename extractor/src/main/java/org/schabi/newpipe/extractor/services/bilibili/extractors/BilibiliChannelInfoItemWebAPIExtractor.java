package org.schabi.newpipe.extractor.services.bilibili.extractors;

import com.grack.nanojson.JsonObject;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class BilibiliChannelInfoItemWebAPIExtractor extends BilibiliBaseChannelInfoItemExtractor {

    public BilibiliChannelInfoItemWebAPIExtractor(final JsonObject json, final String name, final String face, final String uploaderUrl) {
        super(json, name, face, uploaderUrl);
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return item.getString("pic").replace("http:", "https:");
    }

    @Override
    public long getViewCount() throws ParsingException {
        return Optional.of(item.getLong("play"))
                .filter(playCount -> playCount > 0)
                .orElseGet(() -> item.getObject("stat").getLong("view"));
    }

    @SuppressWarnings("SimpleDateFormat")
    @Override
    public String getTextualUploadDate() throws ParsingException {
        if (item.getInt("created") == 0) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((item.getInt("pubdate")) * 1000L));
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((item.getInt("created")) * 1000L));
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return new DateWrapper(LocalDateTime.parse(
                Objects.requireNonNull(getTextualUploadDate()), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atOffset(ZoneOffset.ofHours(+8)));
    }

    @Override
    public boolean requiresMembership() throws ParsingException {
        return item.getInt("elec_arc_type") == 1;
    }
}
