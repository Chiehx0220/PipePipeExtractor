package org.schabi.newpipe.extractor.services.bilibili.extractors;

import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class BilibiliChannelInfoItemClientAPIExtractor extends BilibiliBaseChannelInfoItemExtractor {

    public BilibiliChannelInfoItemClientAPIExtractor(final JsonObject json, final String name, final String face, final String uploaderUrl) {
        super(json, name, face, uploaderUrl);
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return item.getString("cover").replace("http:", "https:");
    }

    @Override
    public long getViewCount() throws ParsingException {
        return Optional.of(item.getLong("play")).orElse(item.getObject("stat").getLong("view"));
    }

    @SuppressWarnings("SimpleDateFormat")
    @Override
    public String getTextualUploadDate() throws ParsingException {
        return item.getString("publish_time_text");
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        long timestampSeconds = item.getLong("ctime", 0);
        return new DateWrapper(LocalDateTime.ofEpochSecond(timestampSeconds, 0, ZoneOffset.ofHours(+8)).atOffset(ZoneOffset.ofHours(+8)));
    }

    @Override
    public boolean requiresMembership() throws ParsingException {
        return item.getArray("badges").toString().contains("充电专属");
    }
}
