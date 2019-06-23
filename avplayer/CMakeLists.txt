cmake_minimum_required(VERSION 3.4.1)
# 添加在native层log库
find_library( # Sets the name of the path variable.
              log-lib
              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

set(distribution_DIR ${CMAKE_SOURCE_DIR}/libs)
include_directories(libs/includes)

# FFmpeg编译出了7个库,这里添加----avutil
add_library( avutil
             SHARED
             IMPORTED )
set_target_properties( avutil
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi-v7a/libavutil.so )

# FFmpeg编译出了7个库,这里添加----swresample
add_library( swresample
             SHARED
             IMPORTED )
set_target_properties( swresample
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi-v7a/libswresample.so )

# FFmpeg编译出了7个库,这里添加----avcodec
add_library( avcodec
             SHARED
             IMPORTED )
set_target_properties( avcodec
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi-v7a/libavcodec.so )

# FFmpeg编译出了7个库,这里添加----avfilter
add_library( avfilter
             SHARED
             IMPORTED)
set_target_properties( avfilter
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi-v7a/libavfilter.so )

# FFmpeg编译出了7个库,这里添加----swscale
add_library( swscale
             SHARED
             IMPORTED)
set_target_properties( swscale
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi-v7a/libswscale.so )

# FFmpeg编译出了7个库,这里添加----avformat
add_library( avformat
             SHARED
             IMPORTED)
set_target_properties( avformat
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi-v7a/libavformat.so )
# FFmpeg编译出了7个库,这里添加----avdevice
add_library( avdevice
             SHARED
             IMPORTED)
set_target_properties( avdevice
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/armeabi-v7a/libavdevice.so )


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_library( avplayer
             SHARED
             src/main/cpp/avplayer.c
            )
add_library( # Specifies the name of the library.
             algorithm

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/algorithm.cpp )

target_link_libraries( # Specifies the target library.
                       avplayer
                       GLESv2 EGL
                       OpenSLES
                       jnigraphics
                       android
                       # 这里需要注意下,下面这些ffmpeg的so库编译是有先后顺序的
                       # 下面的顺序是没问题的,如果想验证编译顺序,可以自己变换顺序试试.
                       avutil swresample swscale avcodec avformat avfilter avdevice

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )