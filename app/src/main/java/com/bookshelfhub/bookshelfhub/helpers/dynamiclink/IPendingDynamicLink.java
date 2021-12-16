package com.bookshelfhub.bookshelfhub.helpers.dynamiclink;

import android.net.Uri;

import androidx.annotation.Nullable;

public interface IPendingDynamicLink {
    public void onComplete(@Nullable Uri ur);
}
