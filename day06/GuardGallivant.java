import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class GuardGallivant {

	private static class Location {
		private int x;
		private int y;

		private Location(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Location)) {
				return false;
			}
			final Location that = (Location) other;
			return this.x == that.x && this.y == that.y;
		}

		@Override
		public int hashCode() {
			int hash = 17;
			hash = hash * 31 + this.x;
			hash = hash * 31 + this.y;
			return hash;
		}

		protected Location clone() {
			return new Location(this.x, this.y);
		}
	}

	private static enum Direction {
		UP('^', new Location(-1, 0)),
		RIGHT('>', new Location(0, 1)),
		DOWN('v', new Location(1, 0)),
		LEFT('<', new Location(0, -1));

		private char value;
		private Location step;

		private Direction(char direction, Location step) {
			this.value = direction;
			this.step = step;
		}

		private Direction rotate() {
			switch (this) {
				case UP: return RIGHT;
				case RIGHT: return DOWN;
				case DOWN: return LEFT;
				default: return UP;
			}
		}

		private Location getNextLocation(Location location) {
			return new Location(location.x + this.step.x, location.y + this.step.y);
		}

		private static Direction getFromChar(char c) {
			switch (c) {
				case '^': return UP;
				case '>': return RIGHT;
				case 'v': return DOWN;
				case '<': return LEFT;
				default: return null;
			}
		}
	}

	private static class Position {
		private Location location;
		private Direction direction;

		private Position(Location location, Direction direction) {
			this.location = location;
			this.direction = direction;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Position)) {
				return false;
			}
			final Position that = (Position) other;
			return this.location.equals(that.location) && this.direction.equals(that.direction);
		}

		@Override
		public int hashCode() {
			int hash = 17;
			hash = hash * 31 + this.location.hashCode();
			hash = hash * 31 + this.direction.hashCode();
			return hash;
		}

		protected Position clone() {
			return new Position(this.location.clone(), this.direction);
		}

		private Location getNextLocation() {
			return this.direction.getNextLocation(this.location);
		}
	}

	private static class Result {
		private int distinctPositions;
		private int obstructions;

		@Override
		public String toString() {
			return String.format("Distinct Positions: %d | Obstructions: %d", this.distinctPositions, this.obstructions);
		}
	}

	private static char[][] readGrid(String filename) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(filename));

		return br.lines()
			.map(String::toCharArray)
			.toArray(char[][]::new);
	}

	private static void printGrid(char[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print(String.format("%c ", grid[i][j]));
			}
			System.out.println();
		}
	}

	private static Position getStartingPosition(char[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				final Direction direction = Direction.getFromChar(grid[i][j]);
				if (direction != null) {
					return new Position(new Location(i, j), direction);
				}
			}
		}
		throw new RuntimeException("No starting position was found.");
	}

	private static boolean isLocationInGrid(Location location, char[][] grid) {
		final int height = grid.length;
		final int width = grid[0].length;
		return location.x >= 0 && location.x < height && location.y >= 0 && location.y < width;
	}

	private static boolean isObstacle(Location location, char[][] grid) {
		return isLocationInGrid(location, grid) && grid[location.x][location.y] == '#';
	}

	private static boolean hasLoop(Position startingPosition, char[][] grid) {
		final Set<Position> positionsVisited = new HashSet<>();
		final Position currentPosition = startingPosition.clone();
		while (isLocationInGrid(currentPosition.location, grid)) {
			if (positionsVisited.contains(currentPosition)) {
				return true;
			}
			positionsVisited.add(currentPosition.clone());
			final Location nextLocation = currentPosition.getNextLocation();
			if (isObstacle(nextLocation, grid)) {
				currentPosition.direction = currentPosition.direction.rotate();
				continue;
			}
			currentPosition.location = nextLocation;
		}
		return false;
	}

	private static Result calculate(char[][] grid) {
		final Position guardPosition = getStartingPosition(grid);
		final Position currentPosition = guardPosition.clone();
		final Set<Location> locationsVisited = new HashSet<>();
		final Result result = new Result();
		result.distinctPositions = 1;

		while (isLocationInGrid(currentPosition.location, grid)) {
			final Location currentLocation = currentPosition.location;
			Direction direction = Direction.getFromChar(grid[currentLocation.x][currentLocation.y]);
			if (direction == null) {
				result.distinctPositions++;
			}
			grid[currentLocation.x][currentLocation.y] = currentPosition.direction.value;
			if (!currentLocation.equals(guardPosition.location)) {
				locationsVisited.add(currentLocation.clone());
			}
			final Location nextLocation = currentPosition.getNextLocation();
			if (isObstacle(nextLocation, grid)) {
				currentPosition.direction = currentPosition.direction.rotate();
				continue;
			}
			currentPosition.location = nextLocation;
		}

		for (Location location : locationsVisited) {
			final char temp = grid[location.x][location.y];
			grid[location.x][location.y] = '#';
			if (hasLoop(guardPosition, grid)) {
				result.obstructions++;
			}
			grid[location.x][location.y] = temp;
		}

		return result;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(calculate(readGrid(args[0])));
	}
}