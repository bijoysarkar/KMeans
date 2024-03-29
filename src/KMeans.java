import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A k-means clustering algorithm implementation.
 * 
 */

public class KMeans {

  public KMeansResult cluster(double[][] centroids, double[][] instances, double threshold) {
    KMeansResult result = new KMeansResult();
    // Centroids are already given an initial 'random' assignment
    // Number of centroids in the initial assignment
    int k = centroids.length;
    // Number of data points
    int n = instances.length;

    int clusterAssignment[] = new int[n];
    int clusterInstanceCount[] = new int[k];
    ArrayList<Double> distortionIterations = new ArrayList<Double>();
    if (k > 0 && n >= k) {

      int iteration_number = 0;
      Double change_in_distortion = Double.MAX_VALUE;
      do {
        reallocateClusterAssignments(centroids, instances, clusterAssignment, clusterInstanceCount);
        // Adjust Empty clusters and recalculate assignments
        for (int i = 0; i < clusterInstanceCount.length; i++) {
          if (clusterInstanceCount[i] == 0) {
            // Get the instance farthest from its centroid and replace this orphan centroid
            System.arraycopy(
                instances[findFarthestFromCentroid(instances, centroids, clusterAssignment)], 0,
                centroids[i], 0, centroids[i].length);
            // Reallocate all cluster assignments
            reallocateClusterAssignments(centroids, instances, clusterAssignment,
                clusterInstanceCount);
            // Reset counter to go over all centroids again
            i = -1;
          }
        }

        // Calculate new Centroids
        reallocateCentroids(instances, centroids, clusterAssignment, clusterInstanceCount);

        distortionIterations.add(calculateDistortion(centroids, instances, clusterAssignment));
        if (iteration_number > 0) {
          change_in_distortion =
              Math.abs((distortionIterations.get(iteration_number) - distortionIterations
                  .get(iteration_number - 1)) / distortionIterations.get(iteration_number - 1));
        }
        iteration_number++;
      } while (change_in_distortion > threshold);

      result.centroids = centroids;
      result.distortionIterations = new double[distortionIterations.size()];
      Iterator<Double> it = distortionIterations.iterator();
      for (int i = 0; i < distortionIterations.size(); i++)
        result.distortionIterations[i] = it.next();
      result.clusterAssignment = clusterAssignment;
    }
    return result;
  }


  private void reallocateClusterAssignments(double[][] centroids, double[][] instances,
      int[] clusterAssignment, int[] clusterInstanceCount) {
    // Reallocate the cluster assignments
    // Reset counts
    Arrays.fill(clusterInstanceCount, 0);
    for (int i = 0; i < clusterAssignment.length; i++) {
      int p = findNearest(instances[i], centroids);
      clusterAssignment[i] = p;
      clusterInstanceCount[clusterAssignment[i]]++;
    }
  }


  private int findFarthestFromCentroid(double[][] instances, double[][] centroids,
      int[] clusterAssignment) {
    int maxDistanceIndex = 0;
    double maxDistanceSquare =
        calculateDistanceSquare(instances[0], centroids[clusterAssignment[0]]);
    for (int i = 1; i < instances.length; i++) {
      double distanceSquare =
          calculateDistanceSquare(instances[i], centroids[clusterAssignment[i]]);
      if (distanceSquare > maxDistanceSquare) {
        maxDistanceSquare = distanceSquare;
        maxDistanceIndex = i;
      }
    }
    return maxDistanceIndex;
  }


  private int findNearest(double[] instance, double[][] centroids) {
    int minDistanceIndex = 0;
    double minDistanceSquare = calculateDistanceSquare(instance, centroids[0]);
    for (int i = 1; i < centroids.length; i++) {
      double distanceSquare = calculateDistanceSquare(instance, centroids[i]);
      // In case of ties the centroid number returned is the one with lower index
      if (distanceSquare < minDistanceSquare) {
        minDistanceSquare = distanceSquare;
        minDistanceIndex = i;
      }
    }
    return minDistanceIndex;
  }


  private double calculateDistanceSquare(double[] point1, double[] point2) {
    double distanceSquare = 0;
    for (int i = 0; i < point1.length; i++) {
      distanceSquare = distanceSquare + Math.pow((point1[i] - point2[i]), 2);
    }
    return distanceSquare;
  }


  private void reallocateCentroids(double instances[][], double[][] centroids,
      int clusterAssignment[], int clusterInstanceCount[]) {
    int n = instances.length;
    int dimensions = instances[0].length;
    for (double c[] : centroids)
      Arrays.fill(c, 0);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < dimensions; j++) {
        centroids[clusterAssignment[i]][j] = centroids[clusterAssignment[i]][j] + instances[i][j];
      }
    }
    for (int i = 0; i < centroids.length; i++) {
      for (int j = 0; j < dimensions; j++) {
        centroids[i][j] = centroids[i][j] / clusterInstanceCount[i];
      }
    }
  }


  private double calculateDistortion(double[][] centroids, double[][] instances,
      int clusterAssignment[]) {
    // Number of centroids in the initial assignment
    int dimensions = centroids[0].length;
    // Number of data points
    int n = instances.length;
    // Calculate distortion;
    double distortion = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < dimensions; j++) {
        distortion =
            distortion + Math.pow((instances[i][j] - centroids[clusterAssignment[i]][j]), 2);
      }
    }
    return distortion;
  }

}
