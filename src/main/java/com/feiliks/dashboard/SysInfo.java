package com.feiliks.dashboard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SysInfo {

    private static Pattern keyNumPt = Pattern.compile("(\\S+):\\s+([0-9]+)");
    private static Pattern spPt = Pattern.compile("\\s+");
    private static Pattern cpuPt = Pattern.compile("cpu[0-9]*");

    public static Map<String, Long> extractKeyNum(String filename) throws IOException {
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

    public static Map<String, Long[]> extractCPUs(String filename) throws IOException {
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

    public static Map<String, Long[]> extractIfaces(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(filename))) {
            Map<String, Long[]> out = new HashMap<>();
            String line;
            do {
                line = br.readLine();
                if (line != null) {
                    String[] cols = spPt.split(line.trim());
                    if (cols.length == 17 && cols[0].endsWith(":")) {
                        String iface = cols[0].substring(0, cols[0].length() - 1);
                        Long[] bytes = new Long[2];
                        bytes[0] = Long.parseLong(cols[1]);
                        bytes[1] = Long.parseLong(cols[9]);
                        out.put(iface, bytes);
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

}
