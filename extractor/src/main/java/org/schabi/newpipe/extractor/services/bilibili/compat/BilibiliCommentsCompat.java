package org.schabi.newpipe.extractor.services.bilibili.compat;

import org.schabi.newpipe.extractor.comments.CommentsExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.bilibili.BilibiliService;
import org.schabi.newpipe.extractor.services.bilibili.WatchDataCache;

import java.io.IOException;

/**
 * {@code BilibiliCommentsLinkHandlerFactory.getId()} resolves a bangumi ("premium series") episode
 * url to its bvid straight out of {@link WatchDataCache}, the same cache
 * {@link BilibiliBulletCommentsCompat} primes for bullet comments - populated only once a stream
 * extractor for the same episode has run. Unlike the cid lookup there, a miss here doesn't throw:
 * {@code getBvid()} returns {@code String} and silently comes back {@code null}, which then blows up
 * one level up wherever that id is used as a real bvid (e.g. {@code getUrl()}'s
 * {@code id.startsWith("BV")}).
 *
 * <p>A caller that builds its comments extractor through here instead of
 * {@link BilibiliService#getCommentsExtractor(String)} directly doesn't have to track that ordering
 * itself: this resolves the episode's bvid first whenever the cache doesn't already have it. Regular
 * (non-bangumi) videos don't need a bvid for their id, so this only ever primes anything for
 * {@code bangumi/play/} urls.
 */
public final class BilibiliCommentsCompat {
    private BilibiliCommentsCompat() {
    }

    public static CommentsExtractor getCommentsExtractor(
            BilibiliService service,
            String url
    ) throws ExtractionException, IOException {
        if (url.contains("bangumi/play/")) {
            ensureBvidCached(service, url);
        }
        return service.getCommentsExtractor(url);
    }

    private static void ensureBvidCached(
            BilibiliService service,
            String url
    ) throws ExtractionException, IOException {
        WatchDataCache cache = service.getWatchDataCache();
        String episodeId = url.split("bangumi/play/")[1].split("\\?")[0];
        if (cache.getBvid(episodeId) != null) {
            return;
        }
        service.getStreamExtractor(url).fetchPage();
    }
}
