; ModuleID = 'test.cpp'
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

%"class.std::ios_base::Init" = type { i8 }
%"class.std::basic_ostream" = type { i32 (...)**, %"class.std::basic_ios" }
%"class.std::basic_ios" = type { %"class.std::ios_base", %"class.std::basic_ostream"*, i8, i8, %"class.std::basic_streambuf"*, %"class.std::ctype"*, %"class.std::num_put"*, %"class.std::num_get"* }
%"class.std::ios_base" = type { i32 (...)**, i64, i64, i32, i32, i32, %"struct.std::ios_base::_Callback_list"*, %"struct.std::ios_base::_Words", [8 x %"struct.std::ios_base::_Words"], i32, %"struct.std::ios_base::_Words"*, %"class.std::locale" }
%"struct.std::ios_base::_Callback_list" = type { %"struct.std::ios_base::_Callback_list"*, void (i32, %"class.std::ios_base"*, i32)*, i32, i32 }
%"struct.std::ios_base::_Words" = type { i8*, i64 }
%"class.std::locale" = type { %"class.std::locale::_Impl"* }
%"class.std::locale::_Impl" = type { i32, %"class.std::locale::facet"**, i64, %"class.std::locale::facet"**, i8** }
%"class.std::locale::facet" = type <{ i32 (...)**, i32, [4 x i8] }>
%"class.std::basic_streambuf" = type { i32 (...)**, i8*, i8*, i8*, i8*, i8*, i8*, %"class.std::locale" }
%"class.std::ctype" = type <{ %"class.std::locale::facet.base", [4 x i8], %struct.__locale_struct*, i8, [7 x i8], i32*, i32*, i16*, i8, [256 x i8], [256 x i8], i8, [6 x i8] }>
%"class.std::locale::facet.base" = type <{ i32 (...)**, i32 }>
%struct.__locale_struct = type { [13 x %struct.__locale_data*], i16*, i32*, i32*, [13 x i8*] }
%struct.__locale_data = type opaque
%"class.std::num_put" = type { %"class.std::locale::facet.base", [4 x i8] }
%"class.std::num_get" = type { %"class.std::locale::facet.base", [4 x i8] }
%class.Box = type { double, double, double }

@_ZStL8__ioinit = internal global %"class.std::ios_base::Init" zeroinitializer, align 1
@__dso_handle = external global i8
@_ZZ4mainE4str1 = private unnamed_addr constant [14 x i8] c"Sample string\00", align 1
@_ZSt4cout = external global %"class.std::basic_ostream", align 8
@.str = private unnamed_addr constant [4 x i8] c"lol\00", align 1
@.str.1 = private unnamed_addr constant [18 x i8] c"Volume of Box2 : \00", align 1
@llvm.global_ctors = appending global [1 x { i32, void ()*, i8* }] [{ i32, void ()*, i8* } { i32 65535, void ()* @_GLOBAL__sub_I_test.cpp, i8* null }]

; Function Attrs: uwtable
define internal void @__cxx_global_var_init() #0 section ".text.startup" {
  call void @_ZNSt8ios_base4InitC1Ev(%"class.std::ios_base::Init"* @_ZStL8__ioinit)
  %1 = call i32 @__cxa_atexit(void (i8*)* bitcast (void (%"class.std::ios_base::Init"*)* @_ZNSt8ios_base4InitD1Ev to void (i8*)*), i8* getelementptr inbounds (%"class.std::ios_base::Init", %"class.std::ios_base::Init"* @_ZStL8__ioinit, i32 0, i32 0), i8* @__dso_handle) #2
  ret void
}

declare void @_ZNSt8ios_base4InitC1Ev(%"class.std::ios_base::Init"*) #1

declare void @_ZNSt8ios_base4InitD1Ev(%"class.std::ios_base::Init"*) #1

; Function Attrs: nounwind
declare i32 @__cxa_atexit(void (i8*)*, i8*, i8*) #2

; Function Attrs: nounwind uwtable
define double @_ZN3Box9getVolumeEv(%class.Box* %this) #3 align 2 {
  %1 = alloca %class.Box*, align 8
  store %class.Box* %this, %class.Box** %1, align 8
  %2 = load %class.Box*, %class.Box** %1, align 8
  %3 = getelementptr inbounds %class.Box, %class.Box* %2, i32 0, i32 0
  %4 = load double, double* %3, align 8
  %5 = getelementptr inbounds %class.Box, %class.Box* %2, i32 0, i32 1
  %6 = load double, double* %5, align 8
  %7 = fmul double %4, %6
  %8 = getelementptr inbounds %class.Box, %class.Box* %2, i32 0, i32 2
  %9 = load double, double* %8, align 8
  %10 = fmul double %7, %9
  ret double %10
}

; Function Attrs: nounwind uwtable
define void @_ZN3Box9setLengthEdS_(%class.Box* noalias sret %agg.result, %class.Box* %this, double %len, %class.Box* byval align 8 %a) #3 align 2 {
  %1 = alloca %class.Box*, align 8
  %2 = alloca double, align 8
  store %class.Box* %this, %class.Box** %1, align 8
  store double %len, double* %2, align 8
  %3 = load %class.Box*, %class.Box** %1, align 8
  %4 = load double, double* %2, align 8
  %5 = getelementptr inbounds %class.Box, %class.Box* %3, i32 0, i32 0
  store double %4, double* %5, align 8
  %6 = bitcast %class.Box* %agg.result to i8*
  %7 = bitcast %class.Box* %3 to i8*
  call void @llvm.memcpy.p0i8.p0i8.i64(i8* %6, i8* %7, i64 24, i32 8, i1 false)
  ret void
}

; Function Attrs: argmemonly nounwind
declare void @llvm.memcpy.p0i8.p0i8.i64(i8* nocapture, i8* nocapture readonly, i64, i32, i1) #4

; Function Attrs: nounwind uwtable
define void @_ZN3Box10setBreadthEd(%class.Box* %this, double %bre) #3 align 2 {
  %1 = alloca %class.Box*, align 8
  %2 = alloca double, align 8
  store %class.Box* %this, %class.Box** %1, align 8
  store double %bre, double* %2, align 8
  %3 = load %class.Box*, %class.Box** %1, align 8
  %4 = load double, double* %2, align 8
  %5 = fadd double %4, 9.000000e+00
  store double %5, double* %2, align 8
  %6 = load double, double* %2, align 8
  %7 = getelementptr inbounds %class.Box, %class.Box* %3, i32 0, i32 1
  store double %6, double* %7, align 8
  ret void
}

; Function Attrs: nounwind uwtable
define void @_ZN3Box9setHeightEd(%class.Box* %this, double %hei) #3 align 2 {
  %1 = alloca %class.Box*, align 8
  %2 = alloca double, align 8
  %myHeight = alloca double, align 8
  store %class.Box* %this, %class.Box** %1, align 8
  store double %hei, double* %2, align 8
  %3 = load %class.Box*, %class.Box** %1, align 8
  %4 = getelementptr inbounds %class.Box, %class.Box* %3, i32 0, i32 2
  %5 = load double, double* %4, align 8
  store double %5, double* %myHeight, align 8
  %6 = load double, double* %2, align 8
  %7 = getelementptr inbounds %class.Box, %class.Box* %3, i32 0, i32 2
  store double %6, double* %7, align 8
  ret void
}

; Function Attrs: norecurse uwtable
define i32 @main() #5 {
  %1 = alloca i32, align 4
  %Box1 = alloca %class.Box, align 8
  %Box2 = alloca %class.Box, align 8
  %volume = alloca double, align 8
  %height1 = alloca double, align 8
  %str1 = alloca [14 x i8], align 1
  %b4 = alloca %class.Box, align 8
  %b3 = alloca %class.Box, align 8
  %2 = alloca %class.Box, align 8
  %3 = alloca %class.Box, align 8
  %4 = alloca %class.Box, align 8
  store i32 0, i32* %1, align 4
  store double 0.000000e+00, double* %volume, align 8
  store double 2.130000e+01, double* %height1, align 8
  %5 = bitcast [14 x i8]* %str1 to i8*
  call void @llvm.memcpy.p0i8.p0i8.i64(i8* %5, i8* getelementptr inbounds ([14 x i8], [14 x i8]* @_ZZ4mainE4str1, i32 0, i32 0), i64 14, i32 1, i1 false)
  %6 = bitcast %class.Box* %b4 to i8*
  call void @llvm.memset.p0i8.i64(i8* %6, i8 0, i64 24, i32 8, i1 false)
  %7 = bitcast %class.Box* %2 to i8*
  %8 = bitcast %class.Box* %Box1 to i8*
  call void @llvm.memcpy.p0i8.p0i8.i64(i8* %7, i8* %8, i64 24, i32 8, i1 false)
  call void @_ZN3Box9setLengthEdS_(%class.Box* sret %b3, %class.Box* %Box1, double 6.000000e+00, %class.Box* byval align 8 %2)
  call void @_ZN3Box10setBreadthEd(%class.Box* %Box1, double 7.000000e+00)
  %9 = load double, double* %height1, align 8
  call void @_ZN3Box9setHeightEd(%class.Box* %Box1, double %9)
  %10 = call double @_ZN3Box9getVolumeEv(%class.Box* %b3)
  %11 = load double, double* %height1, align 8
  %12 = fcmp ogt double %10, %11
  br i1 %12, label %13, label %14

; <label>:13                                      ; preds = %0
  store double 2.220000e+01, double* %volume, align 8
  br label %15

; <label>:14                                      ; preds = %0
  store double 8.756000e+01, double* %volume, align 8
  br label %15

; <label>:15                                      ; preds = %14, %13
  %16 = bitcast %class.Box* %3 to i8*
  %17 = bitcast %class.Box* %b3 to i8*
  call void @llvm.memcpy.p0i8.p0i8.i64(i8* %16, i8* %17, i64 24, i32 8, i1 false)
  call void @_ZN3Box9setLengthEdS_(%class.Box* sret %4, %class.Box* %Box2, double 1.200000e+01, %class.Box* byval align 8 %3)
  call void @_ZN3Box10setBreadthEd(%class.Box* %Box2, double 1.300000e+01)
  call void @_ZN3Box9setHeightEd(%class.Box* %Box2, double 1.000000e+01)
  %18 = call double @_ZN3Box9getVolumeEv(%class.Box* %Box1)
  store double %18, double* %volume, align 8
  %19 = call dereferenceable(272) %"class.std::basic_ostream"* @_ZStlsISt11char_traitsIcEERSt13basic_ostreamIcT_ES5_PKc(%"class.std::basic_ostream"* dereferenceable(272) @_ZSt4cout, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str, i32 0, i32 0))
  %20 = load double, double* %volume, align 8
  %21 = call dereferenceable(272) %"class.std::basic_ostream"* @_ZNSolsEd(%"class.std::basic_ostream"* %19, double %20)
  %22 = call dereferenceable(272) %"class.std::basic_ostream"* @_ZNSolsEPFRSoS_E(%"class.std::basic_ostream"* %21, %"class.std::basic_ostream"* (%"class.std::basic_ostream"*)* @_ZSt4endlIcSt11char_traitsIcEERSt13basic_ostreamIT_T0_ES6_)
  %23 = call double @_ZN3Box9getVolumeEv(%class.Box* %Box2)
  store double %23, double* %volume, align 8
  %24 = call dereferenceable(272) %"class.std::basic_ostream"* @_ZStlsISt11char_traitsIcEERSt13basic_ostreamIcT_ES5_PKc(%"class.std::basic_ostream"* dereferenceable(272) @_ZSt4cout, i8* getelementptr inbounds ([18 x i8], [18 x i8]* @.str.1, i32 0, i32 0))
  %25 = load double, double* %volume, align 8
  %26 = call dereferenceable(272) %"class.std::basic_ostream"* @_ZNSolsEd(%"class.std::basic_ostream"* %24, double %25)
  %27 = call dereferenceable(272) %"class.std::basic_ostream"* @_ZNSolsEPFRSoS_E(%"class.std::basic_ostream"* %26, %"class.std::basic_ostream"* (%"class.std::basic_ostream"*)* @_ZSt4endlIcSt11char_traitsIcEERSt13basic_ostreamIT_T0_ES6_)
  ret i32 0
}

; Function Attrs: argmemonly nounwind
declare void @llvm.memset.p0i8.i64(i8* nocapture, i8, i64, i32, i1) #4

declare dereferenceable(272) %"class.std::basic_ostream"* @_ZStlsISt11char_traitsIcEERSt13basic_ostreamIcT_ES5_PKc(%"class.std::basic_ostream"* dereferenceable(272), i8*) #1

declare dereferenceable(272) %"class.std::basic_ostream"* @_ZNSolsEd(%"class.std::basic_ostream"*, double) #1

declare dereferenceable(272) %"class.std::basic_ostream"* @_ZNSolsEPFRSoS_E(%"class.std::basic_ostream"*, %"class.std::basic_ostream"* (%"class.std::basic_ostream"*)*) #1

declare dereferenceable(272) %"class.std::basic_ostream"* @_ZSt4endlIcSt11char_traitsIcEERSt13basic_ostreamIT_T0_ES6_(%"class.std::basic_ostream"* dereferenceable(272)) #1

; Function Attrs: uwtable
define internal void @_GLOBAL__sub_I_test.cpp() #0 section ".text.startup" {
  call void @__cxx_global_var_init()
  ret void
}

attributes #0 = { uwtable "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #2 = { nounwind }
attributes #3 = { nounwind uwtable "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #4 = { argmemonly nounwind }
attributes #5 = { norecurse uwtable "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.ident = !{!0}

!0 = !{!"clang version 3.8.0-2ubuntu4 (tags/RELEASE_380/final)"}
