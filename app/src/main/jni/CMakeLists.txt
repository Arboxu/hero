cmake_minimum_required(VERSION 3.4.1)

add_library(
             PWEncry
             SHARED
             PWEncry.c )

find_library( # Sets the name of the path variable.
              log-lib
              log )
target_link_libraries(
                       PWEncry
                       ${log-lib} )
