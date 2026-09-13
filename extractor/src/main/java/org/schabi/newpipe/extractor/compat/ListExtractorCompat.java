package org.schabi.newpipe.extractor.compat;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * A caller paging through a {@link ListExtractor} either already has a {@link Page} cursor from a
 * previous call, or it doesn't. The two cases go through different API entry points -
 * {@link ListExtractor#getPage(Page)} for a page you already have a cursor for,
 * {@link ListExtractor#fetchPage()} then {@link ListExtractor#getInitialPage()} for the first one -
 * and {@code getPage(null)} is not a substitute for the first page on every extractor: some (the
 * Bilibili VOD-oriented ones, for one) never implement it and just return null. A caller that pages
 * generically therefore has to special-case "first page vs next page" itself at every call site.
 * This is that one branch, written once, so paging code can call it the same way regardless of
 * which extractor it happens to be driving.
 */
public final class ListExtractorCompat {
    private ListExtractorCompat() {
    }

    public static <R extends InfoItem> ListExtractor.InfoItemsPage<R> fetchInitialOrPage(
            ListExtractor<R> extractor,
            @Nullable Page page
    ) throws IOException, ExtractionException {
        if (page == null) {
            extractor.fetchPage();
            return extractor.getInitialPage();
        }
        return extractor.getPage(page);
    }
}
