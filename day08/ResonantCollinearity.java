import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResonantCollinearity {
    private static class Location {
        private final int x;
        private final int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Location(Location other) {
            this.x = other.x;
            this.y = other.y;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Location otherLocation) {
                return x == otherLocation.x && y == otherLocation.y;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + this.x;
            hash = hash * 31 + this.y;
            return hash;
        }

        private Location getDistance(Location other) {
            return new Location(this.x - other.x, this.y - other.y);
        }

        private Location move(Location distance) {
            return new Location(this.x + distance.x, this.y + distance.y);
        }

        private boolean isWithinBounds(Location mapSize) {
            return this.x >= 0 && this.x < mapSize.x && this.y >= 0 && this.y < mapSize.y;
        }

        private Location negate() {
            return new Location(-this.x, -this.y);
        }
    }

    private static List<String> readMap(String filename) throws IOException {
        final List<String> map = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            while (line != null) {
                map.add(line);
                line = br.readLine();
            }
        }
        return map;
    }

    private static boolean isAlphanumeric(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    private static Map<Character, Set<Location>> generateSatelliteMap(List<String> floor) {
        final Map<Character, Set<Location>> map = new HashMap<>();
        final int height = floor.size();
        final int width = floor.getFirst().length();
        for (int i = 0; i < height; i++) {
            final String row = floor.get(i);
            for (int j = 0; j < width; j++) {
                if (isAlphanumeric(row.charAt(j))) {
                    final Set<Location> locations = map.computeIfAbsent(row.charAt(j), l -> new HashSet<>());
                    locations.add(new Location(j, i));
                }
            }
        }
        return map;
    }

    private static Set<Location> findAntiNodes(Set<Location> locations, Location mapSize) {
        final Set<Location> result = new HashSet<>();
        final Set<Location> copyLocations = new HashSet<>(locations);
        while (!copyLocations.isEmpty()) {
            Location currentLocation = copyLocations.iterator().next();
            copyLocations.remove(currentLocation);
            for (Location neighbor : copyLocations) {
                final Location distance = neighbor.getDistance(currentLocation);
                Location copyCurrentLocation = new Location(currentLocation);
                while (copyCurrentLocation.isWithinBounds(mapSize)) {
                    result.add(copyCurrentLocation);
                    copyCurrentLocation = copyCurrentLocation.move(distance.negate());
                }
                copyCurrentLocation = neighbor;
                while (copyCurrentLocation.isWithinBounds(mapSize)) {
                    result.add(copyCurrentLocation);
                    copyCurrentLocation = copyCurrentLocation.move(distance);
                }
            }
        }
        return result;
    }

    private static Set<Location> findAllAntiNodes(Map<Character, Set<Location>> satelliteMap, Location mapSize) {
        return satelliteMap.values().stream()
                .map(locations -> findAntiNodes(locations, mapSize))
                .reduce(new HashSet<>(), (total, currentSet) -> {
                    total.addAll(currentSet);
                    return total;
                });
    }

    public static void main(String[] args) throws IOException {
        final List<String> map = readMap(args[0]);
        final Location mapSize = new Location(map.getFirst().length(), map.size());
        final Map<Character, Set<Location>> satelliteMap = generateSatelliteMap(map);
        final Set<Location> antiNodes = findAllAntiNodes(satelliteMap, mapSize);
        System.out.println(antiNodes.size());
    }
}
