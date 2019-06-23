package com.beiying.net.download;

/**
 * Created by beiying on 19/4/7.
 */

public class DownloadConfig {

    private int coreThreadSize;
    private int maxThreadSize;
    private int localProgressThreadSize;

    public DownloadConfig(Builder builder) {
        this.coreThreadSize = builder.getCoreThreadSize();
        this.maxThreadSize = builder.getMaxThreadSize();
        this.localProgressThreadSize = builder.getLocalProgressThreadSize();
    }

    public int getCoreThreadSize() {
        return coreThreadSize;
    }

    public int getMaxThreadSize() {
        return maxThreadSize;
    }

    public int getLocalProgressThreadSize() {
        return localProgressThreadSize;
    }

    public static class Builder {
        private int coreThreadSize;
        private int maxThreadSize;
        private int localProgressThreadSize;

        public int getCoreThreadSize() {
            return coreThreadSize;
        }

        public Builder setCoreThreadSize(int coreThreadSize) {
            this.coreThreadSize = coreThreadSize;
            return this;
        }

        public int getMaxThreadSize() {
            return maxThreadSize;
        }

        public Builder setMaxThreadSize(int maxThreadSize) {
            this.maxThreadSize = maxThreadSize;
            return this;
        }

        public int getLocalProgressThreadSize() {
            return localProgressThreadSize;
        }

        public Builder setLocalProgressThreadSize(int localProgressThreadSize) {
            this.localProgressThreadSize = localProgressThreadSize;
            return this;
        }

        public DownloadConfig build() {
            return new DownloadConfig(this);
        }
    }
}
