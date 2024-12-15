import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HoofIt {
    private static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int toInt(int width) {
            return x + width * y;
        }

        public Point add(Point other) {
            return new Point(x + other.x, y + other.y);
        }

        public boolean isInBounds(int width, int height) {
            return x >= 0 && x < width && y >= 0 && y < height;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Point that)) {
                return false;
            }
            return this.x == that.x && this.y == that.y;
        }

        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + this.x;
            hash = hash * 31 + this.y;
            return hash;
        }
    }

    private static Point[] DIRECTIONS = {
            new Point(0, 1),
            new Point(1, 0),
            new Point(0, -1),
            new Point(-1, 0),
    };

    private static String pointsToString(List<Point> points, int width) {
        StringBuilder sb = new StringBuilder();
        for (Point point : points) {
            sb.append(point.toInt(width));
            sb.append("->");
        }
        return sb.toString();
    }

    private static int[][] readMap(String fileName) throws FileNotFoundException {
        final BufferedReader br = new BufferedReader(new FileReader(fileName));
        final char[][] charMap = br.lines()
                .map(String::toCharArray)
                .toArray(char[][]::new);
        final int[][] map = new int[charMap.length][charMap[0].length];
        for (int i = 0; i < charMap.length; i++) {
            for (int j = 0; j < charMap[0].length; j++) {
                map[i][j] = charMap[i][j] - '0';
            }
        }
        return map;
    }

    private static void dfs(int [][] map, Point position, Map<Point, Set<Point>> scoreMap, Map<Point, List<List<Point>>> ratingMap) {
        if (scoreMap.containsKey(position)) {
            return;
        }
        if (map[position.y][position.x] == 9) {
            scoreMap.computeIfAbsent(position, point -> new HashSet<>()).add(position);
            ratingMap.computeIfAbsent(position, point -> new ArrayList<>()).add(List.of(position));
            return;
        }
        final Set<Point> points = new HashSet<>();
        final List<List<Point>> pathList = new ArrayList<>();
        for (Point direction : DIRECTIONS) {
            final Point nextPoint = position.add(direction);
            if (nextPoint.isInBounds(map[0].length, map.length) && map[position.y][position.x] + 1 == map[nextPoint.y][nextPoint.x]) {
                dfs(map, nextPoint, scoreMap, ratingMap);
                points.addAll(scoreMap.get(nextPoint));
                for (List<Point> path : ratingMap.get(nextPoint)) {
                    final List<Point> newPath = new ArrayList<>();
                    newPath.add(position);
                    newPath.addAll(path);
                    pathList.add(newPath);
                }
            }
        }
        scoreMap.put(position, points);
        ratingMap.put(position, pathList);
    }

    public static void main(String[] args) throws FileNotFoundException {
        final int[][] map = readMap(args[0]);
        final Map<Point, Set<Point>> scoreMap = new HashMap<>();
        final Map<Point, List<List<Point>>> ratingMap = new HashMap<>();
        int sum = 0;
        int pathSum = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 0) {
                    final Point position = new Point(x, y);
                    dfs(map, position, scoreMap, ratingMap);
                    sum += scoreMap.get(position).size();
                    pathSum += ratingMap.get(position).stream()
                            .map(l -> pointsToString(l, map[0].length))
                            .collect(Collectors.toSet())
                            .size();
                }
            }
        }
        System.out.println(sum);
        System.out.println(pathSum);
    }
}
