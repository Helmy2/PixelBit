# Keep default constructors (no-arg constructors)
-keepclassmembers class * {
    public <init>();
}

# Keep your model classes used in Firestore
-keep class com.example.pixelbit.domain.model.** { *; }

# Keep annotations (Firestore uses them)
-keepattributes *Annotation*
