package com.kucoin.starter.chaincontext.autoconfig;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "kucoin.chaincontext"
)
public class ChainContextProperties {
    private List<Parser> keys;

    public ChainContextProperties() {
    }

    public List<ChainContextProperties.Parser> getKeys() {
        return this.keys;
    }

    public void setKeys(final List<ChainContextProperties.Parser> keys) {
        this.keys = keys;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ChainContextProperties)) {
            return false;
        } else {
            ChainContextProperties other = (ChainContextProperties)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$keys = this.getKeys();
                Object other$keys = other.getKeys();
                if (this$keys == null) {
                    if (other$keys != null) {
                        return false;
                    }
                } else if (!this$keys.equals(other$keys)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChainContextProperties;
    }



    public String toString() {
        return "ChainContextProperties(keys=" + this.getKeys() + ")";
    }

    public static final class Parser {
        private String key;
        private String parser = "com.kucoin.starter.chaincontext.parser.DefaultRequestKeyParser";

        public Parser(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public String getParser() {
            return this.parser;
        }

        public void setKey(final String key) {
            this.key = key;
        }

        public void setParser(final String parser) {
            this.parser = parser;
        }

        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof ChainContextProperties.Parser)) {
                return false;
            } else {
                ChainContextProperties.Parser other = (ChainContextProperties.Parser)o;
                Object this$key = this.getKey();
                Object other$key = other.getKey();
                if (this$key == null) {
                    if (other$key != null) {
                        return false;
                    }
                } else if (!this$key.equals(other$key)) {
                    return false;
                }

                Object this$parser = this.getParser();
                Object other$parser = other.getParser();
                if (this$parser == null) {
                    if (other$parser != null) {
                        return false;
                    }
                } else if (!this$parser.equals(other$parser)) {
                    return false;
                }

                return true;
            }
        }


        public String toString() {
            return "ChainContextProperties.Parser(key=" + this.getKey() + ", parser=" + this.getParser() + ")";
        }

        public Parser() {
        }

        public Parser(final String key, final String parser) {
            this.key = key;
            this.parser = parser;
        }
    }
}

