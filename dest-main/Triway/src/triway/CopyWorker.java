package triway;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.*;
import static triway.Home.jTable1;

public class CopyWorker {

    public PausableSwingWorker operation;
    String sourceDirectory;
    String destinationDirectory;
    public static int fileCopiedDb;
    public static long fileCopiedSizeDb;
    public static String timeToCopy;


    public static PausableSwingWorker createAndShowGUI(int row, int col, File srcDir, File destDir) {


        final PausableSwingWorker<Void, ProgressMonitorE.CopyData> worker = new PausableSwingWorker<Void, ProgressMonitorE.CopyData>() {

            @Override
            protected Void doInBackground() throws Exception {
                copyFiles(row, col, srcDir, destDir);
                return null;
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
            }
        });

        Action pause = new AbstractAction("Pause") {
            @Override
            public void actionPerformed(ActionEvent e) {
                worker.pause();
            }
        };

        Action resume = new AbstractAction("Resume") {
            @Override
            public void actionPerformed(ActionEvent e) {
                worker.resume();
            }
        };

        worker.execute();

        return worker;
    }

    abstract static class PausableSwingWorker<K, V> extends SwingWorker<K, V> {

        private volatile boolean isPaused;

        private int row;
        private int col;
        private long startTime;


        public List<CopyData> copyFiles(int row, int col, File srcDir, File destDir) {

            this.row = row;
            this.col = col;
            this.startTime = System.nanoTime();


            if (!isPaused()) {
                int progress = 0;
                // initialize bound property progress (inherited from SwingWorker)
                setProgress(0);
                // get the files to be copied from the source directory
                File[] files = srcDir.listFiles();
                fileCopiedDb = files.length;
                // determine the scope of the task
                long totalBytes = calcTotalBytes(srcDir);
                fileCopiedSizeDb = calcTotalBytes(srcDir);
                long bytesCopied = 0;

                while (progress < 100) {

                    if (!isPaused()) {

                        //System.out.println(Thread.currentThread().getName());
                        // copy the files to the destination directory
                        for (File f : files) {


                            File destFile = new File(destDir, f.getName());
                            long previousLen = 0;
                            //taskOutput.append("Now Copying "+ f.getName() + "\n");
                            try {
                                InputStream in = new FileInputStream(f);
                                OutputStream out = new FileOutputStream(destFile);
                                byte[] buf = new byte[12000000];
                                int counter = 0;
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    if (!isPaused()) {
                                        out.write(buf, 0, len);
                                        counter += len;
                                        bytesCopied += (destFile.length() - previousLen);
                                        previousLen = destFile.length();
                                        int PROGRESS_CHECKPOINT = 0;
                                        if (counter > PROGRESS_CHECKPOINT || bytesCopied == totalBytes) {
                                            // get % complete for the task
                                            progress = (int) ((100 * bytesCopied) / totalBytes);
                                            counter = 0;
                                            CopyData current = new CopyData(progress, f.getName(), getTotalKiloBytes(totalBytes), getKiloBytesCopied(bytesCopied), timeToString((((totalBytes - bytesCopied) * 100) * (totalBytes / bytesCopied))), isPaused());

                                            setProgress(progress);
                                            publish((V) current);
                                        }
                                    } else {
                                        try {
                                            Thread.sleep(2000);


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                in.close();
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(2000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                try {

                    Thread.sleep(2000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        public final void pause() {
            if (!isPaused() && !isDone()) {
                isPaused = true;

                firePropertyChange("paused", false, true);
            }
        }

        public final void resume() {
            if (isPaused() && !isDone()) {
                isPaused = false;
                firePropertyChange("paused", true, false);
            }
        }

        public final boolean isPaused() {
            return isPaused;
        }

        public String timeToString(long nanos) {

            Optional<TimeUnit> first = Stream.of(DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, MICROSECONDS).filter(u -> u.convert(nanos, NANOSECONDS) > 0).findFirst();
            TimeUnit unit = first.isPresent() ? first.get() : NANOSECONDS;

            double value = (double) nanos / NANOSECONDS.convert(1, unit);
            //return String.format("%.4g %s", value, unit.name().toLowerCase());
            return String.format("%.4g %s", value, "(mins)");
        }

        long getTotalKiloBytes(long totalBytes) {
            return totalBytes / 1000;
        }

        long getKiloBytesCopied(long bytesCopied) {
            return bytesCopied / 1000;
        }

        long calcTotalBytes(File directory) {
            long length = 0;
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += calcTotalBytes(file);
            }
            return length;
        }

        @Override
        protected void process(List<V> data) {
            if (isCancelled()) {
                return;
            }
            CopyData update = new CopyData(0, "", 0, 0, "", false);
            for (V d : data) {
                // progress updates may be batched, so get the most recent
                if (((CopyData) d).getKiloBytesCopied() > update.getKiloBytesCopied()) {
                    update = (CopyData) d;
                }
            }
            DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
            if (update.getProgress() < 100 && !update.getisPaused()) {
                tableModel.setValueAt((int) update.getProgress(), row, col);

            } else if (update.getProgress() < 100 && update.getisPaused()) {
            } else {
                tableModel.setValueAt((int) update.getProgress(), row, col);
            }
        }

        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            //progressMonitor.setProgress(0);
            long endTime = System.nanoTime();
            
            long a = endTime - this.startTime;
            
            
            //timeToCopy = timeToString(a);
            double value = (((double) a) / 1000000000)/60;
            timeToCopy =  String.format("%.4g %s",value,"(mins)");
            
        }
    }
}
