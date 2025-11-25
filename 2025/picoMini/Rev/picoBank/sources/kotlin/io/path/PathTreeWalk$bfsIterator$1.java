package kotlin.io.path;

import java.nio.file.Path;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.sequences.SequenceScope;

@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H@"}, d2 = {"<anonymous>", "", "Lkotlin/sequences/SequenceScope;", "Ljava/nio/file/Path;"}, k = 3, mv = {1, 8, 0}, xi = 48)
@DebugMetadata(c = "kotlin.io.path.PathTreeWalk$bfsIterator$1", f = "PathTreeWalk.kt", i = {0, 0, 0, 0, 0, 0, 1, 1, 1}, l = {184, 190}, m = "invokeSuspend", n = {"$this$iterator", "queue", "entriesReader", "pathNode", "this_$iv", "path$iv", "$this$iterator", "queue", "entriesReader"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "L$0", "L$1", "L$2"})
/* compiled from: PathTreeWalk.kt */
final class PathTreeWalk$bfsIterator$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> {
    private /* synthetic */ Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    Object L$4;
    Object L$5;
    int label;
    final /* synthetic */ PathTreeWalk this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    PathTreeWalk$bfsIterator$1(PathTreeWalk pathTreeWalk, Continuation<? super PathTreeWalk$bfsIterator$1> continuation) {
        super(2, continuation);
        this.this$0 = pathTreeWalk;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        PathTreeWalk$bfsIterator$1 pathTreeWalk$bfsIterator$1 = new PathTreeWalk$bfsIterator$1(this.this$0, continuation);
        pathTreeWalk$bfsIterator$1.L$0 = obj;
        return pathTreeWalk$bfsIterator$1;
    }

    public final Object invoke(SequenceScope<? super Path> sequenceScope, Continuation<? super Unit> continuation) {
        return ((PathTreeWalk$bfsIterator$1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x010c  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0153 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0096  */
    public final java.lang.Object invokeSuspend(java.lang.Object r18) {
        /*
            r17 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            r1 = r17
            int r2 = r1.label
            r3 = 0
            switch(r2) {
                case 0: goto L_0x004c;
                case 1: goto L_0x002a;
                case 2: goto L_0x0014;
                default: goto L_0x000c;
            }
        L_0x000c:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r2)
            throw r0
        L_0x0014:
            r2 = r17
            r4 = r18
            r5 = 0
            java.lang.Object r6 = r2.L$2
            kotlin.io.path.DirectoryEntriesReader r6 = (kotlin.io.path.DirectoryEntriesReader) r6
            java.lang.Object r7 = r2.L$1
            kotlin.collections.ArrayDeque r7 = (kotlin.collections.ArrayDeque) r7
            java.lang.Object r8 = r2.L$0
            kotlin.sequences.SequenceScope r8 = (kotlin.sequences.SequenceScope) r8
            kotlin.ResultKt.throwOnFailure(r4)
            goto L_0x0151
        L_0x002a:
            r2 = r17
            r4 = r18
            r5 = 0
            java.lang.Object r6 = r2.L$5
            java.nio.file.Path r6 = (java.nio.file.Path) r6
            java.lang.Object r7 = r2.L$4
            kotlin.io.path.PathTreeWalk r7 = (kotlin.io.path.PathTreeWalk) r7
            java.lang.Object r8 = r2.L$3
            kotlin.io.path.PathNode r8 = (kotlin.io.path.PathNode) r8
            java.lang.Object r9 = r2.L$2
            kotlin.io.path.DirectoryEntriesReader r9 = (kotlin.io.path.DirectoryEntriesReader) r9
            java.lang.Object r10 = r2.L$1
            kotlin.collections.ArrayDeque r10 = (kotlin.collections.ArrayDeque) r10
            java.lang.Object r11 = r2.L$0
            kotlin.sequences.SequenceScope r11 = (kotlin.sequences.SequenceScope) r11
            kotlin.ResultKt.throwOnFailure(r4)
            goto L_0x00ea
        L_0x004c:
            kotlin.ResultKt.throwOnFailure(r18)
            r2 = r17
            r4 = r18
            java.lang.Object r5 = r2.L$0
            kotlin.sequences.SequenceScope r5 = (kotlin.sequences.SequenceScope) r5
            kotlin.collections.ArrayDeque r6 = new kotlin.collections.ArrayDeque
            r6.<init>()
            kotlin.io.path.DirectoryEntriesReader r7 = new kotlin.io.path.DirectoryEntriesReader
            kotlin.io.path.PathTreeWalk r8 = r2.this$0
            boolean r8 = r8.getFollowLinks()
            r7.<init>(r8)
            kotlin.io.path.PathNode r8 = new kotlin.io.path.PathNode
            kotlin.io.path.PathTreeWalk r9 = r2.this$0
            java.nio.file.Path r9 = r9.start
            kotlin.io.path.PathTreeWalk r10 = r2.this$0
            java.nio.file.Path r10 = r10.start
            kotlin.io.path.PathTreeWalk r11 = r2.this$0
            java.nio.file.LinkOption[] r11 = r11.getLinkOptions()
            java.lang.Object r10 = kotlin.io.path.PathTreeWalkKt.keyOf(r10, r11)
            r8.<init>(r9, r10, r3)
            r6.addLast(r8)
            r8 = r5
            r16 = r7
            r7 = r6
            r6 = r16
        L_0x008b:
            r5 = r7
            java.util.Collection r5 = (java.util.Collection) r5
            boolean r5 = r5.isEmpty()
            r9 = 1
            r5 = r5 ^ r9
            if (r5 == 0) goto L_0x0153
            java.lang.Object r5 = r7.removeFirst()
            kotlin.io.path.PathNode r5 = (kotlin.io.path.PathNode) r5
            kotlin.io.path.PathTreeWalk r10 = r2.this$0
            r11 = r8
            r12 = 0
            java.nio.file.Path r13 = r5.getPath()
            java.nio.file.LinkOption[] r14 = r10.getLinkOptions()
            int r15 = r14.length
            java.lang.Object[] r14 = java.util.Arrays.copyOf(r14, r15)
            java.nio.file.LinkOption[] r14 = (java.nio.file.LinkOption[]) r14
            int r15 = r14.length
            java.lang.Object[] r14 = java.util.Arrays.copyOf(r14, r15)
            java.nio.file.LinkOption[] r14 = (java.nio.file.LinkOption[]) r14
            boolean r14 = java.nio.file.Files.isDirectory(r13, r14)
            if (r14 == 0) goto L_0x0124
            boolean r14 = kotlin.io.path.PathTreeWalkKt.createsCycle(r5)
            if (r14 != 0) goto L_0x011a
            boolean r14 = r10.getIncludeDirectories()
            if (r14 == 0) goto L_0x00f4
            r14 = r2
            kotlin.coroutines.Continuation r14 = (kotlin.coroutines.Continuation) r14
            r2.L$0 = r8
            r2.L$1 = r7
            r2.L$2 = r6
            r2.L$3 = r5
            r2.L$4 = r10
            r2.L$5 = r13
            r2.label = r9
            java.lang.Object r9 = r11.yield(r13, r14)
            if (r9 != r0) goto L_0x00e0
            return r0
        L_0x00e0:
            r9 = r6
            r11 = r8
            r6 = r13
            r8 = r5
            r5 = r12
            r16 = r10
            r10 = r7
            r7 = r16
        L_0x00ea:
            r12 = r5
            r13 = r6
            r5 = r8
            r6 = r9
            r8 = r11
            r16 = r10
            r10 = r7
            r7 = r16
        L_0x00f4:
            java.nio.file.LinkOption[] r9 = r10.getLinkOptions()
            int r10 = r9.length
            java.lang.Object[] r9 = java.util.Arrays.copyOf(r9, r10)
            java.nio.file.LinkOption[] r9 = (java.nio.file.LinkOption[]) r9
            int r10 = r9.length
            java.lang.Object[] r9 = java.util.Arrays.copyOf(r9, r10)
            java.nio.file.LinkOption[] r9 = (java.nio.file.LinkOption[]) r9
            boolean r9 = java.nio.file.Files.isDirectory(r13, r9)
            if (r9 == 0) goto L_0x008b
            java.util.List r5 = r6.readEntries(r5)
            r9 = 0
            r10 = r5
            java.util.Collection r10 = (java.util.Collection) r10
            r7.addAll(r10)
            goto L_0x008b
        L_0x011a:
            java.nio.file.FileSystemLoopException r0 = new java.nio.file.FileSystemLoopException
            java.lang.String r3 = r13.toString()
            r0.<init>(r3)
            throw r0
        L_0x0124:
            java.nio.file.LinkOption[] r5 = new java.nio.file.LinkOption[r9]
            r10 = 0
            java.nio.file.LinkOption r14 = java.nio.file.LinkOption.NOFOLLOW_LINKS
            r5[r10] = r14
            java.lang.Object[] r5 = java.util.Arrays.copyOf(r5, r9)
            java.nio.file.LinkOption[] r5 = (java.nio.file.LinkOption[]) r5
            boolean r5 = java.nio.file.Files.exists(r13, r5)
            if (r5 == 0) goto L_0x008b
            r5 = r2
            kotlin.coroutines.Continuation r5 = (kotlin.coroutines.Continuation) r5
            r2.L$0 = r8
            r2.L$1 = r7
            r2.L$2 = r6
            r2.L$3 = r3
            r2.L$4 = r3
            r2.L$5 = r3
            r9 = 2
            r2.label = r9
            java.lang.Object r5 = r11.yield(r13, r5)
            if (r5 != r0) goto L_0x0150
            return r0
        L_0x0150:
            r5 = r12
        L_0x0151:
            goto L_0x008b
        L_0x0153:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathTreeWalk$bfsIterator$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
