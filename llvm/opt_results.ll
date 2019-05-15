; ModuleID = 'result.ll'

%class.Main = type { i32, i32 }

@.str.1 = private unnamed_addr constant [4 x i8] c"lol\00"

; Function Attrs: norecurse nounwind readnone
define i32 @main() #0 {
  ret i32 1758
}

; Function Attrs: norecurse nounwind
define i32 @Main.main(%class.Main* nocapture %self) #1 {
  %1 = getelementptr %class.Main, %class.Main* %self, i64 0, i32 0
  store i32 1758, i32* %1, align 4
  ret i32 1758
}

; Function Attrs: norecurse nounwind readnone
define i8* @Main.testString(%class.Main* nocapture readnone %self, i8* nocapture readnone %myString) #0 {
  ret i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.1, i64 0, i64 0)
}

; Function Attrs: norecurse nounwind readnone
define i32 @Main.truc(%class.Main* nocapture readnone %self, i32 %tail, %class.Main* nocapture readnone %boolean) #0 {
  %1 = add i32 %tail, 3
  ret i32 %1
}

attributes #0 = { norecurse nounwind readnone }
attributes #1 = { norecurse nounwind }
