package com.bookshelfhub.bookshelfhub.helpers.dynamiclink;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class FirebaseDLink {

    public FirebaseDLink(){

    }

     public void getDeepLinkAsync(Activity activity, IPendingDynamicLink pendingDynamicLink){
      FirebaseDynamicLinks.getInstance()
                .getDynamicLink(activity.getIntent())
                .addOnSuccessListener(activity, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(@Nullable PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        if (pendingDynamicLinkData != null) {
                            pendingDynamicLink.onComplete(pendingDynamicLinkData.getLink());
                        }else{
                            pendingDynamicLink.onComplete(null);
                        }
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pendingDynamicLink.onComplete(null);
                    }
                });
    }

}
