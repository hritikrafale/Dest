package triway;

//import org.apache.log4j.PropertyConfigurator;

public class CopyData {

    //final static Logger logger = Logger.getLogger(CopyData.class);
    
    private long i;
    private String s;
    private long i0;
    private long i1;
    private String t;
    private boolean isPaused;

    public CopyData(long i, String s, long i0, long i1, String t, boolean isPaused) {
        this.i = i;
        this.s = s;
        this.i0 = i0;
        this.i1 = i1;
        this.t = t;
        this.isPaused = isPaused;
    }


    long getProgress() {
        return i;
    }

    long getKiloBytesCopied() {
        return i1;
    }

    boolean getisPaused() {
        return isPaused;
    }
}