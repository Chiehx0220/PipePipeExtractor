package org.schabi.newpipe.extractor.services.bilibili.extractors;

import static org.schabi.newpipe.extractor.services.bilibili.utils.getDurationFromString;

import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;

import java.util.Optional;

/**
 * A video item inside a Bilibili channel/uploads listing, shared between the two upstream response
 * shapes ({@link BilibiliChannelInfoItemClientAPIExtractor the app "Client API"} and
 * {@link BilibiliChannelInfoItemWebAPIExtractor the web "Web API"}) - every field the two APIs agree
 * on lives here; each subclass only supplies the handful they disagree on (thumbnail field name,
 * view-count fallback, upload-date format, membership-gate field).
 */
public abstract class BilibiliBaseChannelInfoItemExtractor implements StreamInfoItemExtractor {

    protected final JsonObject item;
    private final String name;
    private final String face;
    private final String uploaderUrl;

    protected BilibiliBaseChannelInfoItemExtractor(final JsonObject json, final String name, final String face, final String uploaderUrl) {
        item = json;
        this.name = name;
        this.face = face;
        this.uploaderUrl = uploaderUrl;
    }

    @Override
    public String getName() throws ParsingException {
        return item.getString("title");
    }

    @Override
    public String getUrl() throws ParsingException {
        return "https://www.bilibili.com/video/" + item.getString("bvid") + "?p=1";
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public long getDuration() throws ParsingException {
        if (item.getLong("duration") != 0) {
            return item.getLong("duration");
        }
        return getDurationFromString(item.getString("length"));
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return Optional.ofNullable(item.getString("author")).orElse(name);
    }

    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        return face;
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        // This listing's own items don't carry a per-item uploader url - it's the channel we're
        // already browsing, so it comes from the extractor that built this listing (see callers of
        // this constructor), same as name/face above.
        return uploaderUrl == null ? "" : uploaderUrl;
    }
}
