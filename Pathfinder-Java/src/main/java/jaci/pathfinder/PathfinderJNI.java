package jaci.pathfinder;

import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.SwerveModifier;

import java.io.IOException;

public class PathfinderJNI {

    static boolean libLoaded = false;

    static {
        if (!libLoaded) {
            try {
                String
                        env = System.getProperty("os.name", "generic").toLowerCase(),
                        path = "/";
                boolean is64 = System.getProperty("os.arch", "32").contains("64");

                if (env.contains("win")) { // Windows environment
                    path += "windows/";
                    path += "x86" + (is64 ? "-64" : "") + "/";
                    NativeUtils.loadLibraryFromJar(path + "pathfinderjava.dll");
                } else if (env.contains("mac")) { // macOS environment
                    path += "osx/";
                    path += "x86" + (is64 ? "-64" : "") + "/";
                    NativeUtils.loadLibraryFromJar(path + "libpathfinderjava.dylib");
                } else if (env.contains("nux")) { // Unix environment
                    path += "linux/";
                    path += "x86" + (is64 ? "-64" : "") + "/";
                    NativeUtils.loadLibraryFromJar(path + "libpathfinderjava.so");
                } else {
                    System.out.println("WARNING: pathfinderjava NOT LOADED! On OS " + env);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            libLoaded = true;
        }
    }

    public static Trajectory generateTrajectory(Waypoint[] waypoints, Trajectory.Config c) {
        return new Trajectory(generateTrajectory(waypoints, c.fit, c.sample_count, c.dt, c.max_velocity, c.max_acceleration, c.max_jerk));
    }
    public static native Trajectory.Segment[] generateTrajectory(Waypoint[] waypoints, Trajectory.FitMethod fit, int samples, double dt, double max_velocity, double max_acceleration, double max_jerk);

    public static Trajectory[] modifyTrajectoryTank(Trajectory traj, double wheelbase_width) {
        Trajectory.Segment[][] mod = modifyTrajectoryTank(traj.segments, wheelbase_width);
        return new Trajectory[] { new Trajectory(mod[0]), new Trajectory(mod[1]) };
    }
    public static native Trajectory.Segment[][] modifyTrajectoryTank(Trajectory.Segment[] source, double wheelbase_width);

    public static Trajectory[] modifyTrajectorySwerve(Trajectory traj, double wheelbase_width, double wheelbase_depth, SwerveModifier.Mode mode) {
        Trajectory.Segment[][] mod = modifyTrajectorySwerve(traj.segments, wheelbase_width, wheelbase_depth, mode);
        return new Trajectory[] { new Trajectory(mod[0]), new Trajectory(mod[1]), new Trajectory(mod[2]), new Trajectory(mod[3]) };
    }
    public static native Trajectory.Segment[][] modifyTrajectorySwerve(Trajectory.Segment[] source, double wheelbase_width, double wheelbase_depth, SwerveModifier.Mode mode);

    public static native void trajectorySerialize(Trajectory.Segment[] source, String filename);
    public static native Trajectory.Segment[] trajectoryDeserialize(String filename);

    public static native void trajectorySerializeCSV(Trajectory.Segment[] source, String filename);
    public static native Trajectory.Segment[] trajectoryDeserializeCSV(String filename);
}
