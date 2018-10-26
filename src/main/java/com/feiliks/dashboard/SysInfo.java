package com.feiliks.dashboard;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SysInfo {

    private static Pattern keyNumPt = Pattern.compile("(\\S+):\\s+([0-9]+)");
    private static Pattern spPt = Pattern.compile("\\s+");
    private static Pattern cpuPt = Pattern.compile("cpu[0-9]*");
    private static Pattern devPt = Pattern.compile("/dev/([a-zA-Z0-9]+)");

    static Map<String, Long> extractKeyNum(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(filename))) {
            Map<String, Long> out = new HashMap<>();
            String line;
            do {
                line = br.readLine();
                if (line != null) {
                    Matcher m = keyNumPt.matcher(line);
                    if (m.find()) {
                        out.put(m.group(1), Long.parseLong(m.group(2)));
                    }
                }
            } while (line != null);
            return out;
        }
    }

    public static long[] getMemoryUsage() {
        try {
            long[] out = new long[2];
            Map<String, Long> meminfo = extractKeyNum("/proc/meminfo");
            Long total = meminfo.get("MemTotal");
            Long avail = meminfo.get("MemAvailable");
            out[0] = total - avail;
            out[1] = avail;
            return out;
        } catch (IOException e) {
            return null;
        }
    }

    static Map<String, Long[]> extractCPUs(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(filename))) {
            Map<String, Long[]> out = new HashMap<>();
            String line;
            do {
                line = br.readLine();
                if (line != null) {
                    String[] cols = spPt.split(line);
                    if (cols.length > 1) {
                        if (cpuPt.matcher(cols[0]).find()) {
                            try {
                                List<Long> values = new LinkedList<>();
                                for (int i = 1; i < cols.length; i++)
                                    values.add(Long.parseLong(cols[i]));
                                out.put(cols[0], values.toArray(new Long[0]));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            } while (line != null);
            return out;
        }
    }

    public static Map<String, Long[]> getCPUUsage() {
        try {
            Map<String, Long[]> out = new HashMap<>();
            for (Map.Entry<String, Long[]> cpu : extractCPUs("/proc/stat").entrySet()) {
                Long[] data = new Long[2];
                data[0] = 0L; // total
                for (Long n : cpu.getValue())
                    data[0] += n;
                data[1] = data[0] - cpu.getValue()[3]; // total - idle
                out.put(cpu.getKey(), data);
            }
            return out;
        } catch (IOException e) {
            return null;
        }
    }

    static Map<String, Long[]> extractIfaces(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(filename))) {
            Map<String, Long[]> out = new HashMap<>();
            String line;
            do {
                line = br.readLine();
                if (line != null) {
                    String[] cols = spPt.split(line.trim());
                    if (cols.length == 17 && cols[0].endsWith(":")) {
                        try {
                            String iface = cols[0].substring(0, cols[0].length() - 1);
                            Long[] bytes = new Long[2];
                            bytes[0] = Long.parseLong(cols[1]);
                            bytes[1] = Long.parseLong(cols[9]);
                            out.put(iface, bytes);
                        } catch (NumberFormatException ex) {
                        }
                    }
                }
            } while (line != null);
            return out;
        }
    }

    public static Map<String, Long[]> getIfaces() {
        try {
            return extractIfaces("/proc/net/dev");
        } catch (IOException e) {
            return null;
        }
    }

    public static class DiskInfo {
        private long reads; // 4
        private long sectorsRead; // 6
        private long timeReading; // 7
        private long writes; // 8
        private long sectorsWritten; // 10
        private long timeWriting; // 11
        private long used;
        private long available;
        private String pathMounted;

        public long getReads() {
            return reads;
        }

        public long getSectorsRead() {
            return sectorsRead;
        }

        public long getTimeReading() {
            return timeReading;
        }

        public long getWrites() {
            return writes;
        }

        public long getSectorsWritten() {
            return sectorsWritten;
        }

        public long getTimeWriting() {
            return timeWriting;
        }

        public long getUsed() {
            return used;
        }

        public long getAvailable() {
            return available;
        }

        public String getPathMounted() {
            return pathMounted;
        }
    }

    static Map<String, Long[]> extractDiskstats(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(filename))) {
            Map<String, Long[]> out = new HashMap<>();
            String line;
            do {
                line = br.readLine();
                if (line != null) {
                    String[] cols = spPt.split(line.trim());
                    if (cols.length > 13 && (cols[2].startsWith("sd") ||
                            cols[2].startsWith("hd"))) {
                        try {
                            Long[] values = new Long[11];
                            for (int i = 3; i < 14; i++) {
                                values[i - 3] = Long.parseLong(cols[i]);
                            }
                            out.put(cols[2], values);
                        } catch (NumberFormatException ex) {
                        }
                    }
                }
            } while (line != null);
            ArrayList<String> toRemove = new ArrayList<>();
            for (String dev : out.keySet()) {
                for (String dev2 : out.keySet()) {
                    if (dev.equals(dev2)) continue;
                    if (dev2.startsWith(dev))
                        toRemove.add(dev);
                }
            }
            for (String dev : toRemove)
                out.remove(dev);
            return out;
        }
    }

    public static Map<String, Long[]> getDiskIO() {
        try {
            Map<String, Long[]> out = new HashMap<>();
            for (Map.Entry<String, Long[]> entry : extractDiskstats(
                    "/proc/diskstats").entrySet()) {
                Long[] io = new Long[3];
                Long[] values = entry.getValue();
                io[0] = values[2]; // sectors read
                io[1] = values[6]; // sectors written
                io[2] = values[3] + values[7]; // active time (read + write, millisecs)
                out.put(entry.getKey(), io);
            }
            return out;
        } catch (IOException ex) {
            return null;
        }
    }

    static Map<String, DiskInfo> extractDiskInfo() throws IOException {
        Map<String, Long[]> disks = extractDiskstats("/proc/diskstats");
        Process p = new ProcessBuilder("df").start();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()))) {
            Map<String, DiskInfo> out = new HashMap<>();
            String line;
            do {
                line = br.readLine();
                if (line != null) {
                    String[] cols = spPt.split(line.trim());
                    if (cols.length == 6) {
                        Matcher m = devPt.matcher(cols[0]);
                        if (m.find()) {
                            String dev = m.group(1);
                            if (disks.containsKey(dev)) {
                                try {
                                    DiskInfo disk = new DiskInfo();
                                    Long[] values = disks.get(dev);
                                    disk.reads = values[0];
                                    disk.sectorsRead = values[2];
                                    disk.timeReading = values[3];
                                    disk.writes = values[4];
                                    disk.sectorsWritten = values[6];
                                    disk.timeWriting = values[7];
                                    disk.used = Long.parseLong(cols[2]);
                                    disk.available = Long.parseLong(cols[3]);
                                    disk.pathMounted = cols[5];
                                    out.put(dev, disk);
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                }
            } while (line != null);
            return out;
        }
    }

    public static Map<String, DiskInfo> getDiskInfo() {
        try {
            return extractDiskInfo();
        } catch (IOException ignored) {
            return null;
        }
    }

}
