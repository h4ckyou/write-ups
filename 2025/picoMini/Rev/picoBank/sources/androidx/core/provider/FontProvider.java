package androidx.core.provider;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.RemoteException;
import android.util.Log;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.provider.FontsContractCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class FontProvider {
    private static final Comparator<byte[]> sByteArrayComparator = new FontProvider$$ExternalSyntheticLambda0();

    private FontProvider() {
    }

    static FontsContractCompat.FontFamilyResult getFontFamilyResult(Context context, FontRequest request, CancellationSignal cancellationSignal) throws PackageManager.NameNotFoundException {
        ProviderInfo providerInfo = getProvider(context.getPackageManager(), request, context.getResources());
        if (providerInfo == null) {
            return FontsContractCompat.FontFamilyResult.create(1, (FontsContractCompat.FontInfo[]) null);
        }
        return FontsContractCompat.FontFamilyResult.create(0, query(context, request, providerInfo.authority, cancellationSignal));
    }

    static ProviderInfo getProvider(PackageManager packageManager, FontRequest request, Resources resources) throws PackageManager.NameNotFoundException {
        String providerAuthority = request.getProviderAuthority();
        ProviderInfo info = packageManager.resolveContentProvider(providerAuthority, 0);
        if (info == null) {
            throw new PackageManager.NameNotFoundException("No package found for authority: " + providerAuthority);
        } else if (info.packageName.equals(request.getProviderPackage())) {
            List<byte[]> signatures = convertToByteArrayList(packageManager.getPackageInfo(info.packageName, 64).signatures);
            Collections.sort(signatures, sByteArrayComparator);
            List<List<byte[]>> requestCertificatesList = getCertificates(request, resources);
            for (int i = 0; i < requestCertificatesList.size(); i++) {
                List<byte[]> requestSignatures = new ArrayList<>(requestCertificatesList.get(i));
                Collections.sort(requestSignatures, sByteArrayComparator);
                if (equalsByteArrayList(signatures, requestSignatures)) {
                    return info;
                }
            }
            return null;
        } else {
            throw new PackageManager.NameNotFoundException("Found content provider " + providerAuthority + ", but package was not " + request.getProviderPackage());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x0122  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static androidx.core.provider.FontsContractCompat.FontInfo[] query(android.content.Context r21, androidx.core.provider.FontRequest r22, java.lang.String r23, android.os.CancellationSignal r24) {
        /*
            r1 = r23
            java.lang.String r0 = "result_code"
            java.lang.String r2 = "font_italic"
            java.lang.String r3 = "font_weight"
            java.lang.String r4 = "font_ttc_index"
            java.lang.String r5 = "file_id"
            java.lang.String r6 = "_id"
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            android.net.Uri$Builder r8 = new android.net.Uri$Builder
            r8.<init>()
            java.lang.String r9 = "content"
            android.net.Uri$Builder r8 = r8.scheme(r9)
            android.net.Uri$Builder r8 = r8.authority(r1)
            android.net.Uri r8 = r8.build()
            android.net.Uri$Builder r10 = new android.net.Uri$Builder
            r10.<init>()
            android.net.Uri$Builder r9 = r10.scheme(r9)
            android.net.Uri$Builder r9 = r9.authority(r1)
            java.lang.String r10 = "file"
            android.net.Uri$Builder r9 = r9.appendPath(r10)
            android.net.Uri r9 = r9.build()
            r17 = 0
            r15 = r21
            androidx.core.provider.FontProvider$ContentQueryWrapper r18 = androidx.core.provider.FontProvider.ContentQueryWrapper.make(r15, r8)
            r10 = 7
            java.lang.String[] r12 = new java.lang.String[r10]     // Catch:{ all -> 0x011f }
            r14 = 0
            r12[r14] = r6     // Catch:{ all -> 0x011f }
            r13 = 1
            r12[r13] = r5     // Catch:{ all -> 0x011f }
            r10 = 2
            r12[r10] = r4     // Catch:{ all -> 0x011f }
            java.lang.String r10 = "font_variation_settings"
            r11 = 3
            r12[r11] = r10     // Catch:{ all -> 0x011f }
            r10 = 4
            r12[r10] = r3     // Catch:{ all -> 0x011f }
            r10 = 5
            r12[r10] = r2     // Catch:{ all -> 0x011f }
            r10 = 6
            r12[r10] = r0     // Catch:{ all -> 0x011f }
            java.lang.String r16 = "query = ?"
            java.lang.String[] r11 = new java.lang.String[r13]     // Catch:{ all -> 0x011f }
            java.lang.String r10 = r22.getQuery()     // Catch:{ all -> 0x011f }
            r11[r14] = r10     // Catch:{ all -> 0x011f }
            r19 = 0
            r10 = r18
            r20 = r11
            r11 = r8
            r13 = r16
            r1 = r14
            r14 = r20
            r15 = r19
            r16 = r24
            android.database.Cursor r10 = r10.query(r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x011f }
            if (r10 == 0) goto L_0x010c
            int r11 = r10.getCount()     // Catch:{ all -> 0x0108 }
            if (r11 <= 0) goto L_0x010c
            int r0 = r10.getColumnIndex(r0)     // Catch:{ all -> 0x0108 }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ all -> 0x0108 }
            r11.<init>()     // Catch:{ all -> 0x0108 }
            r7 = r11
            int r6 = r10.getColumnIndex(r6)     // Catch:{ all -> 0x0108 }
            int r5 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x0108 }
            int r4 = r10.getColumnIndex(r4)     // Catch:{ all -> 0x0108 }
            int r3 = r10.getColumnIndex(r3)     // Catch:{ all -> 0x0108 }
            int r2 = r10.getColumnIndex(r2)     // Catch:{ all -> 0x0108 }
        L_0x00a5:
            boolean r11 = r10.moveToNext()     // Catch:{ all -> 0x0108 }
            if (r11 == 0) goto L_0x0104
            r11 = -1
            if (r0 == r11) goto L_0x00b3
            int r14 = r10.getInt(r0)     // Catch:{ all -> 0x0108 }
            goto L_0x00b4
        L_0x00b3:
            r14 = r1
        L_0x00b4:
            r13 = r14
            if (r4 == r11) goto L_0x00bc
            int r14 = r10.getInt(r4)     // Catch:{ all -> 0x0108 }
            goto L_0x00bd
        L_0x00bc:
            r14 = r1
        L_0x00bd:
            if (r5 != r11) goto L_0x00d0
            long r15 = r10.getLong(r6)     // Catch:{ all -> 0x0108 }
            r19 = r15
            r16 = r2
            r1 = r19
            android.net.Uri r17 = android.content.ContentUris.withAppendedId(r8, r1)     // Catch:{ all -> 0x0108 }
            r1 = r17
            goto L_0x00dc
        L_0x00d0:
            r16 = r2
            long r1 = r10.getLong(r5)     // Catch:{ all -> 0x0108 }
            android.net.Uri r17 = android.content.ContentUris.withAppendedId(r9, r1)     // Catch:{ all -> 0x0108 }
            r1 = r17
        L_0x00dc:
            if (r3 == r11) goto L_0x00e3
            int r2 = r10.getInt(r3)     // Catch:{ all -> 0x0108 }
            goto L_0x00e5
        L_0x00e3:
            r2 = 400(0x190, float:5.6E-43)
        L_0x00e5:
            r15 = r16
            if (r15 == r11) goto L_0x00f4
            int r11 = r10.getInt(r15)     // Catch:{ all -> 0x0108 }
            r16 = r0
            r0 = 1
            if (r11 != r0) goto L_0x00f7
            r11 = r0
            goto L_0x00f8
        L_0x00f4:
            r16 = r0
            r0 = 1
        L_0x00f7:
            r11 = 0
        L_0x00f8:
            androidx.core.provider.FontsContractCompat$FontInfo r0 = androidx.core.provider.FontsContractCompat.FontInfo.create(r1, r14, r2, r11, r13)     // Catch:{ all -> 0x0108 }
            r7.add(r0)     // Catch:{ all -> 0x0108 }
            r2 = r15
            r0 = r16
            r1 = 0
            goto L_0x00a5
        L_0x0104:
            r16 = r0
            r15 = r2
            goto L_0x010c
        L_0x0108:
            r0 = move-exception
            r17 = r10
            goto L_0x0120
        L_0x010c:
            if (r10 == 0) goto L_0x0111
            r10.close()
        L_0x0111:
            r18.close()
            r0 = 0
            androidx.core.provider.FontsContractCompat$FontInfo[] r0 = new androidx.core.provider.FontsContractCompat.FontInfo[r0]
            java.lang.Object[] r0 = r7.toArray(r0)
            androidx.core.provider.FontsContractCompat$FontInfo[] r0 = (androidx.core.provider.FontsContractCompat.FontInfo[]) r0
            return r0
        L_0x011f:
            r0 = move-exception
        L_0x0120:
            if (r17 == 0) goto L_0x0125
            r17.close()
        L_0x0125:
            r18.close()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.provider.FontProvider.query(android.content.Context, androidx.core.provider.FontRequest, java.lang.String, android.os.CancellationSignal):androidx.core.provider.FontsContractCompat$FontInfo[]");
    }

    private static List<List<byte[]>> getCertificates(FontRequest request, Resources resources) {
        if (request.getCertificates() != null) {
            return request.getCertificates();
        }
        return FontResourcesParserCompat.readCerts(resources, request.getCertificatesArrayResId());
    }

    static /* synthetic */ int lambda$static$0(byte[] l, byte[] r) {
        if (l.length != r.length) {
            return l.length - r.length;
        }
        for (int i = 0; i < l.length; i++) {
            if (l[i] != r[i]) {
                return l[i] - r[i];
            }
        }
        return 0;
    }

    private static boolean equalsByteArrayList(List<byte[]> signatures, List<byte[]> requestSignatures) {
        if (signatures.size() != requestSignatures.size()) {
            return false;
        }
        for (int i = 0; i < signatures.size(); i++) {
            if (!Arrays.equals(signatures.get(i), requestSignatures.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatures) {
        List<byte[]> shaList = new ArrayList<>();
        for (Signature signature : signatures) {
            shaList.add(signature.toByteArray());
        }
        return shaList;
    }

    private interface ContentQueryWrapper {
        void close();

        Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal);

        static ContentQueryWrapper make(Context context, Uri uri) {
            return new ContentQueryWrapperApi24Impl(context, uri);
        }
    }

    private static class ContentQueryWrapperApi16Impl implements ContentQueryWrapper {
        private final ContentProviderClient mClient;

        ContentQueryWrapperApi16Impl(Context context, Uri uri) {
            this.mClient = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        }

        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
            if (this.mClient == null) {
                return null;
            }
            try {
                return this.mClient.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
            } catch (RemoteException e) {
                Log.w("FontsProvider", "Unable to query the content provider", e);
                return null;
            }
        }

        public void close() {
            if (this.mClient != null) {
                this.mClient.release();
            }
        }
    }

    private static class ContentQueryWrapperApi24Impl implements ContentQueryWrapper {
        private final ContentProviderClient mClient;

        ContentQueryWrapperApi24Impl(Context context, Uri uri) {
            this.mClient = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        }

        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
            if (this.mClient == null) {
                return null;
            }
            try {
                return this.mClient.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
            } catch (RemoteException e) {
                Log.w("FontsProvider", "Unable to query the content provider", e);
                return null;
            }
        }

        public void close() {
            if (this.mClient != null) {
                this.mClient.close();
            }
        }
    }
}
