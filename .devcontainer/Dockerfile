FROM ubuntu:20.04

# Install dependencies
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    android-sdk \
    curl \
    unzip \
    git \
    && apt-get clean

# Set environment variables
ENV ANDROID_HOME=/usr/lib/android-sdk
ENV PATH=$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH

# Create a non-root user
RUN useradd -ms /bin/bash vscode
USER vscode