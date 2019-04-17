; ModuleID = 'test.c'
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str = private unnamed_addr constant [19 x i8] c"Enter an integer: \00", align 1
@.str.1 = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@.str.2 = private unnamed_addr constant [53 x i8] c"Error! Factorial of a negative number doesn't exist.\00", align 1
@.str.3 = private unnamed_addr constant [23 x i8] c"Factorial of %d = %llu\00", align 1

; Function Attrs: nounwind uwtable
define i32 @main() #0 {
  %n = alloca i32, align 4
  %1 = bitcast i32* %n to i8*
  call void @llvm.lifetime.start(i64 4, i8* %1) #3
  %2 = tail call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([19 x i8], [19 x i8]* @.str, i64 0, i64 0))
  %3 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1, i64 0, i64 0), i32* nonnull %n)
  %4 = load i32, i32* %n, align 4, !tbaa !1
  %5 = icmp slt i32 %4, 0
  br i1 %5, label %13, label %.preheader

.preheader:                                       ; preds = %0
  %6 = icmp slt i32 %4, 1
  br i1 %6, label %._crit_edge, label %.lr.ph

.lr.ph:                                           ; preds = %.preheader
  %7 = sext i32 %4 to i64
  %8 = sext i32 %4 to i64
  %9 = add nsw i64 %8, -1
  %xtraiter = and i64 %8, 7
  %lcmp.mod = icmp eq i64 %xtraiter, 0
  br i1 %lcmp.mod, label %.lr.ph.split, label %.preheader5

.preheader5:                                      ; preds = %.lr.ph
  br label %10

; <label>:10                                      ; preds = %.preheader5, %10
  %indvars.iv.prol = phi i64 [ %indvars.iv.next.prol, %10 ], [ 1, %.preheader5 ]
  %factorial.02.prol = phi i64 [ %11, %10 ], [ 1, %.preheader5 ]
  %prol.iter = phi i64 [ %prol.iter.sub, %10 ], [ %xtraiter, %.preheader5 ]
  %11 = mul i64 %factorial.02.prol, %indvars.iv.prol
  %indvars.iv.next.prol = add nuw nsw i64 %indvars.iv.prol, 1
  %prol.iter.sub = add i64 %prol.iter, -1
  %prol.iter.cmp = icmp eq i64 %prol.iter.sub, 0
  br i1 %prol.iter.cmp, label %.lr.ph.split.loopexit, label %10, !llvm.loop !5

.lr.ph.split.loopexit:                            ; preds = %10
  %indvars.iv.next.prol.lcssa = phi i64 [ %indvars.iv.next.prol, %10 ]
  %.lcssa7 = phi i64 [ %11, %10 ]
  br label %.lr.ph.split

.lr.ph.split:                                     ; preds = %.lr.ph.split.loopexit, %.lr.ph
  %indvars.iv.unr = phi i64 [ 1, %.lr.ph ], [ %indvars.iv.next.prol.lcssa, %.lr.ph.split.loopexit ]
  %factorial.02.unr = phi i64 [ 1, %.lr.ph ], [ %.lcssa7, %.lr.ph.split.loopexit ]
  %.lcssa.unr = phi i64 [ undef, %.lr.ph ], [ %.lcssa7, %.lr.ph.split.loopexit ]
  %12 = icmp ult i64 %9, 7
  br i1 %12, label %._crit_edge.loopexit, label %.lr.ph.split.split

.lr.ph.split.split:                               ; preds = %.lr.ph.split
  br label %15

; <label>:13                                      ; preds = %0
  %14 = call i32 (i8*, ...) @printf(i8* nonnull getelementptr inbounds ([53 x i8], [53 x i8]* @.str.2, i64 0, i64 0))
  br label %26

; <label>:15                                      ; preds = %15, %.lr.ph.split.split
  %indvars.iv = phi i64 [ %indvars.iv.unr, %.lr.ph.split.split ], [ %indvars.iv.next.7, %15 ]
  %factorial.02 = phi i64 [ %factorial.02.unr, %.lr.ph.split.split ], [ %23, %15 ]
  %16 = mul i64 %factorial.02, %indvars.iv
  %indvars.iv.next = add nuw nsw i64 %indvars.iv, 1
  %17 = mul i64 %16, %indvars.iv.next
  %indvars.iv.next.1 = add nsw i64 %indvars.iv, 2
  %18 = mul i64 %17, %indvars.iv.next.1
  %indvars.iv.next.2 = add nsw i64 %indvars.iv, 3
  %19 = mul i64 %18, %indvars.iv.next.2
  %indvars.iv.next.3 = add nsw i64 %indvars.iv, 4
  %20 = mul i64 %19, %indvars.iv.next.3
  %indvars.iv.next.4 = add nsw i64 %indvars.iv, 5
  %21 = mul i64 %20, %indvars.iv.next.4
  %indvars.iv.next.5 = add nsw i64 %indvars.iv, 6
  %22 = mul i64 %21, %indvars.iv.next.5
  %indvars.iv.next.6 = add nsw i64 %indvars.iv, 7
  %23 = mul i64 %22, %indvars.iv.next.6
  %indvars.iv.next.7 = add nsw i64 %indvars.iv, 8
  %24 = icmp slt i64 %indvars.iv.next.6, %7
  br i1 %24, label %15, label %._crit_edge.loopexit.unr-lcssa

._crit_edge.loopexit.unr-lcssa:                   ; preds = %15
  %.lcssa6 = phi i64 [ %23, %15 ]
  br label %._crit_edge.loopexit

._crit_edge.loopexit:                             ; preds = %.lr.ph.split, %._crit_edge.loopexit.unr-lcssa
  %.lcssa = phi i64 [ %.lcssa.unr, %.lr.ph.split ], [ %.lcssa6, %._crit_edge.loopexit.unr-lcssa ]
  br label %._crit_edge

._crit_edge:                                      ; preds = %._crit_edge.loopexit, %.preheader
  %factorial.0.lcssa = phi i64 [ 1, %.preheader ], [ %.lcssa, %._crit_edge.loopexit ]
  %25 = call i32 (i8*, ...) @printf(i8* nonnull getelementptr inbounds ([23 x i8], [23 x i8]* @.str.3, i64 0, i64 0), i32 %4, i64 %factorial.0.lcssa)
  br label %26

; <label>:26                                      ; preds = %._crit_edge, %13
  call void @llvm.lifetime.end(i64 4, i8* %1) #3
  ret i32 0
}

; Function Attrs: argmemonly nounwind
declare void @llvm.lifetime.start(i64, i8* nocapture) #1

; Function Attrs: nounwind
declare i32 @printf(i8* nocapture readonly, ...) #2

; Function Attrs: nounwind
declare i32 @__isoc99_scanf(i8* nocapture readonly, ...) #2

; Function Attrs: argmemonly nounwind
declare void @llvm.lifetime.end(i64, i8* nocapture) #1

attributes #0 = { nounwind uwtable "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { argmemonly nounwind }
attributes #2 = { nounwind "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #3 = { nounwind }

!llvm.ident = !{!0}

!0 = !{!"clang version 3.8.0-2ubuntu4 (tags/RELEASE_380/final)"}
!1 = !{!2, !2, i64 0}
!2 = !{!"int", !3, i64 0}
!3 = !{!"omnipotent char", !4, i64 0}
!4 = !{!"Simple C/C++ TBAA"}
!5 = distinct !{!5, !6}
!6 = !{!"llvm.loop.unroll.disable"}
