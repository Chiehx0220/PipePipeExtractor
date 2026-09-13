package org.schabi.newpipe.extractor.services.bilibili.compat;

import org.schabi.newpipe.extractor.bulletComments.BulletCommentsExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.bilibili.BilibiliService;
import org.schabi.newpipe.extractor.services.bilibili.WatchDataCache;

import java.io.IOException;

/**
 * {@code BilibiliBulletCommentsExtractor.onFetchPage()} reads this video's cid straight out of
 * {@link WatchDataCache}, which is only populated once a stream extractor for the same video id has
 * run in this process - an ordering the bullet-comments extractor itself neither documents nor
 * enforces, so a caller that gets it wrong sees a bare {@link NullPointerException} instead of
 * anything actionable.
 *
 * <p>A caller that builds its bullet-comments extractor through here instead of
 * {@link BilibiliService#getBulletCommentsExtractor(String)} directly doesn't have to track that
 * ordering itself: this resolves the video's cid first - via the same stream extractor Bilibili's
 * cid always comes from - whenever the cache doesn't already have it, live URLs excepted (they
 * don't need a cid at all).
 */
public final class BilibiliBulletCommentsCompat {
    private BilibiliBulletCommentsCompat() {
    }

    public static BulletCommentsExtractor getBulletCommentsExtractor(
            BilibiliService service,
            String url
    ) throws ExtractionException, IOException {
        if (!url.contains(BilibiliService.LIVE_BASE_URL)) {
            ensureCidCached(service, url);
        }
        return service.getBulletCommentsExtractor(url);
    }

    private static void ensureCidCached(
            BilibiliService service,
            String url
    ) throws ExtractionException, IOException {
        WatchDataCache cache = service.getWatchDataCache();
        String id = service.getStreamLHFactory().getId(url);
        if (cache.hasCid(id)) {
            return;
        }
        service.getStreamExtractor(url).fetchPage();
    }
}
