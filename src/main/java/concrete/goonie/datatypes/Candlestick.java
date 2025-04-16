package concrete.goonie.datatypes;

public  class Candlestick {
        private final double open;
        private final double high;
        private final double low;
        private final double close;

        public Candlestick(double open, double high, double low, double close) {
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
        }

        public double getOpen() { return open; }
        public double getHigh() { return high; }
        public double getLow() { return low; }
        public double getClose() { return close; }

        @Override
        public String toString() {
            return "Candlestick{" +
                    "open=" + open +
                    ", high=" + high +
                    ", low=" + low +
                    ", close=" + close +
                    '}';
        }
    }