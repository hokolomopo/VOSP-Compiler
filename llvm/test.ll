; ModuleID = 'test.c'
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

%struct.Child = type { %struct.ChildVTable*, i32, i32 }
%struct.ChildVTable = type { i8* (%struct.Child*)*, void (i32)*, {}* }

@.str = private unnamed_addr constant [8 x i8] c"Someone\00", align 1
@.str.1 = private unnamed_addr constant [5 x i8] c"Hey\0A\00", align 1
@.str.2 = private unnamed_addr constant [4 x i8] c"%s\0A\00", align 1

; Function Attrs: nounwind uwtable
define i8* @newMethod(%struct.Child* %self, i32 %x) #0 {
  %1 = alloca %struct.Child*, align 8
  %2 = alloca i32, align 4
  store %struct.Child* %self, %struct.Child** %1, align 8
  store i32 %x, i32* %2, align 4
  ret i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str, i32 0, i32 0)
}

; Function Attrs: nounwind uwtable
define i8* @inheritedMethod(%struct.Child* %self) #0 {
  %1 = alloca %struct.Child*, align 8
  store %struct.Child* %self, %struct.Child** %1, align 8
  ret i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str, i32 0, i32 0)
}

; Function Attrs: nounwind uwtable
define void @overriddenMethod(i32 %x) #0 {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str.1, i32 0, i32 0))
  ret void
}

declare i32 @printf(i8*, ...) #1

; Function Attrs: nounwind uwtable
define i32 @main() #0 {
  %1 = alloca i32, align 4
  %x = alloca %struct.Child*, align 8
  %s = alloca i8*, align 8
  store i32 0, i32* %1, align 4
  %2 = call noalias i8* @malloc(i64 16) #3
  %3 = bitcast i8* %2 to %struct.Child*
  store %struct.Child* %3, %struct.Child** %x, align 8
  %4 = call noalias i8* @malloc(i64 24) #3
  %5 = bitcast i8* %4 to %struct.ChildVTable*
  %6 = load %struct.Child*, %struct.Child** %x, align 8
  %7 = getelementptr inbounds %struct.Child, %struct.Child* %6, i32 0, i32 0
  store %struct.ChildVTable* %5, %struct.ChildVTable** %7, align 8
  %8 = load %struct.Child*, %struct.Child** %x, align 8
  %9 = getelementptr inbounds %struct.Child, %struct.Child* %8, i32 0, i32 0
  %10 = load %struct.ChildVTable*, %struct.ChildVTable** %9, align 8
  %11 = getelementptr inbounds %struct.ChildVTable, %struct.ChildVTable* %10, i32 0, i32 2
  %12 = bitcast {}** %11 to i8* (%struct.Child*, i32)**
  store i8* (%struct.Child*, i32)* @newMethod, i8* (%struct.Child*, i32)** %12, align 8
  %13 = load %struct.Child*, %struct.Child** %x, align 8
  %14 = getelementptr inbounds %struct.Child, %struct.Child* %13, i32 0, i32 0
  %15 = load %struct.ChildVTable*, %struct.ChildVTable** %14, align 8
  %16 = getelementptr inbounds %struct.ChildVTable, %struct.ChildVTable* %15, i32 0, i32 2
  %17 = bitcast {}** %16 to i8* (%struct.Child*, i32)**
  %18 = load i8* (%struct.Child*, i32)*, i8* (%struct.Child*, i32)** %17, align 8
  %19 = load %struct.Child*, %struct.Child** %x, align 8
  %20 = call i8* %18(%struct.Child* %19, i32 3)
  store i8* %20, i8** %s, align 8
  %21 = load i8*, i8** %s, align 8
  %22 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.2, i32 0, i32 0), i8* %21)
  %23 = load %struct.Child*, %struct.Child** %x, align 8
  %24 = getelementptr inbounds %struct.Child, %struct.Child* %23, i32 0, i32 0
  %25 = load %struct.ChildVTable*, %struct.ChildVTable** %24, align 8
  %26 = getelementptr inbounds %struct.ChildVTable, %struct.ChildVTable* %25, i32 0, i32 1
  store void (i32)* @overriddenMethod, void (i32)** %26, align 8
  %27 = load %struct.Child*, %struct.Child** %x, align 8
  %28 = getelementptr inbounds %struct.Child, %struct.Child* %27, i32 0, i32 0
  %29 = load %struct.ChildVTable*, %struct.ChildVTable** %28, align 8
  %30 = getelementptr inbounds %struct.ChildVTable, %struct.ChildVTable* %29, i32 0, i32 1
  %31 = load void (i32)*, void (i32)** %30, align 8
  call void %31(i32 2)
  ret i32 0
}

; Function Attrs: nounwind
declare noalias i8* @malloc(i64) #2

attributes #0 = { nounwind uwtable "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #2 = { nounwind "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #3 = { nounwind }

!llvm.ident = !{!0}

!0 = !{!"clang version 3.8.0-2ubuntu4 (tags/RELEASE_380/final)"}
