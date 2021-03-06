package me.crack3dc0d3.minetopiavehiclesrevamp.main.util;

import me.crack3dc0d3.minetopiavehiclesrevamp.main.Main;
import me.crack3dc0d3.minetopiavehiclesrevamp.main.api.enums.VehicleType;
import me.crack3dc0d3.minetopiavehiclesrevamp.main.api.vehicle.Seat;
import me.crack3dc0d3.minetopiavehiclesrevamp.main.api.vehicle.Vehicle;
import me.crack3dc0d3.minetopiavehiclesrevamp.main.api.vehicle.VehicleManager;
import me.crack3dc0d3.minetopiavehiclesrevamp.main.util.enums.Messages;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class VehicleMover {

    public static Vector addRightVelocity(ArmorStand as, int direction, float vectorSize) {
        Vector vel = as.getVelocity().clone();
        Vector newVel = vel.clone();

        if (direction == 1) {
        newVel.setX(-vel.getZ());
        newVel.setZ(vel.getX());
        } else {
        newVel.setX(vel.getZ());
        newVel.setZ(-vel.getX());
        }

        newVel = newVel.normalize().multiply(vectorSize);
        return newVel;
        }




    public static void doMovement(InputManager manager, Player p) {

        boolean w, a, s, d, space;

        w = manager.isW();
        a = manager.isA();
        s = manager.isS();
        d = manager.isD();
        space = manager.isSpace();


        int rotspeed = 6;
        Vehicle vehicle = null;
        for (Vehicle vehicle1 : VehicleManager.getVehicles()) {
            if(Seat.getSeat((ArmorStand) p.getVehicle()).getMainVehicle() == null) {
                return;
            }
            if (Seat.getSeat((ArmorStand) p.getVehicle()).getMainVehicle() == vehicle1) {
                vehicle = vehicle1;
            }
        }

        if (vehicle == null) {
            return;
        }

        if(!w && !s && !a && !d && !space) {
            vehicle.getMainStand().setVelocity(new Vector(0, 0, 0));
        }
        if(vehicle.getFuelLevel() <= 0) {
            vehicle.getMainStand().setVelocity(new Vector(0, 0, 0));
            return;
        }

        boolean cruiseControlEnabled = space && Main.getSettings().getConfig().getBoolean("enable-cruisecontrol") && vehicle.getType() == VehicleType.CAR;

        if (w) {
            if (vehicle.getCurSpeed() < vehicle.getSpeed()) {
                if (vehicle.getCurSpeed() < 0) {
                    vehicle.setCurSpeed(vehicle.getCurSpeed() + (vehicle.getOptrekSpeed() + 0.02D));
                } else {
                    vehicle.setCurSpeed(vehicle.getCurSpeed() + vehicle.getOptrekSpeed());
                }
            } else {
                vehicle.setCurSpeed(vehicle.getSpeed());
            }
        } else if (s) {
            if (vehicle.getCurSpeed() > -(vehicle.getSpeed() / 4D)) {
                if (vehicle.getCurSpeed() > 0) {
                    vehicle.setCurSpeed(vehicle.getCurSpeed() - (vehicle.getOptrekSpeed() + 0.02D));
                } else {
                    vehicle.setCurSpeed(vehicle.getCurSpeed() - vehicle.getOptrekSpeed());
                }
            } else {
                vehicle.setCurSpeed(-(vehicle.getSpeed() / 4D));
            }
        } else if(!cruiseControlEnabled) {
            double breakSpeed = Main.getInstance().getSettings().getConfig().getDouble("breakSpeed");

            if(vehicle.getCurSpeed() >= -(breakSpeed) && vehicle.getCurSpeed() <= breakSpeed) {
                vehicle.setCurSpeed(0);
            }

            if(vehicle.getCurSpeed() < breakSpeed && vehicle.getCurSpeed() != 0) {
                vehicle.setCurSpeed(vehicle.getCurSpeed() + breakSpeed);
            }

            if(vehicle.getCurSpeed() > breakSpeed && vehicle.getCurSpeed() != 0) {
                vehicle.setCurSpeed(vehicle.getCurSpeed() - breakSpeed);
            }

//            if(vehicle.getCurSpeed() < breakSpeed && ) {
//                vehicle.setCurSpeed(vehicle.getCurSpeed() + breakSpeed);
//            } else if(vehicle.getCurSpeed() > breakSpeed && vehicle.getCurSpeed() != 0.0){
//                vehicle.setCurSpeed(vehicle.getCurSpeed() - breakSpeed);
//            } else {
//                vehicle.setCurSpeed(0);
//            }



        }
        if(vehicle.getType() == VehicleType.HELICOPTER) {
            if (space) {
                if (vehicle.getCurUpSpeed() < vehicle.getMaxUpSpeed()) {
                    if (vehicle.getCurUpSpeed() < 0) {
                        vehicle.setCurUpSpeed(vehicle.getUpSpeed());
                    } else {
                        vehicle.setCurUpSpeed(vehicle.getCurUpSpeed() + vehicle.getUpSpeed());
                    }
                } else {
                    vehicle.setCurUpSpeed(vehicle.getMaxUpSpeed());
                }
            }
            if(!space && !vehicle.getMainStand().isOnGround()) {
                if(vehicle.getCurUpSpeed() > 0) {
                    vehicle.setCurUpSpeed(0);
                }
                if (vehicle.getCurUpSpeed() - vehicle.getDownSpeed() < -(vehicle.getMaxDownSpeed())) {
                    vehicle.setCurUpSpeed(-(vehicle.getMaxDownSpeed()));
                }
                if(vehicle.getCurUpSpeed() > -(vehicle.getMaxDownSpeed())) {
                    vehicle.setCurUpSpeed(vehicle.getCurUpSpeed() - vehicle.getDownSpeed());
                }

            }
            if(vehicle.getMainStand().isOnGround() && !space) {
                vehicle.setCurUpSpeed(0);
            }
        }
        Vector toAdd = new Vector();
        if (a) {
            Location loc = vehicle.getMainStand().getLocation().clone();
            if (vehicle.getCurSpeed() > 0) {
                loc.setYaw((float) (loc.getYaw() - rotspeed));
                toAdd = addRightVelocity(vehicle.getMainStand(), -1, (float) vehicle.getCurSpeed() / 20f);

            } else if (vehicle.getCurSpeed() < 0) {
                loc.setYaw((float) (loc.getYaw() +  rotspeed));
            } else if (vehicle.getCurSpeed() == 0) {
                loc.setYaw((float) (loc.getYaw() - rotspeed));
            }
            Methods.setPosition(vehicle.getMainStand(), loc);
        }
        if (d) {
            Location loc = vehicle.getMainStand().getLocation().clone();
            if (vehicle.getCurSpeed() > 0) {

                loc.setYaw((float) (loc.getYaw() + rotspeed));
                toAdd = addRightVelocity(vehicle.getMainStand(), 1, (float) vehicle.getCurSpeed() / 20f);

            } else if (vehicle.getCurSpeed() < 0) {
                loc.setYaw((float) (loc.getYaw() - rotspeed));
            } else if (vehicle.getCurSpeed() == 0) {
                loc.setYaw((float) (loc.getYaw() + rotspeed));
            }
            Methods.setPosition(vehicle.getMainStand(), loc);
        }



        if(vehicle.getType() == VehicleType.HELICOPTER) {
            if(vehicle.getMainStand().isOnGround()) {
                vehicle.setCurSpeed(0);
                toAdd = new Vector(0,0,0);
            }
        }


        vehicle.getMainStand().setGravity(true);
        Vector main = new Vector(vehicle.getMainStand().getLocation().getDirection().multiply(0.5).getX(), -1 * vehicle.getCurSpeed(), vehicle.getMainStand().getLocation().getDirection().multiply(0.5).getZ()).multiply(vehicle.getCurSpeed());
        if(vehicle.getType() == VehicleType.HELICOPTER) {
            main = new Vector(vehicle.getMainStand().getLocation().getDirection().multiply(0.5).getX(), vehicle.getCurUpSpeed(), vehicle.getMainStand().getLocation().getDirection().multiply(0.5).getZ()).multiply(vehicle.getCurSpeed());
            main.setY(vehicle.getCurUpSpeed());
            //Bukkit.broadcastMessage( "" + vehicle.getCurUpSpeed());
        }
        vehicle.getMainStand().setVelocity((main.add(toAdd)));
        if(vehicle.getMainStand().getLocation().getY() >= Main.getInstance().getSettings().getConfig().getInt("max-helicopter-height")) {
            Location toLoc = new Location(vehicle.getMainStand().getLocation().getWorld(), vehicle.getMainStand().getLocation().getX(), Main.getInstance().getSettings().getConfig().getInt("max-helicopter-height") - 2, vehicle.getMainStand().getLocation().getZ());
            vehicle.setCurUpSpeed(0);
            Methods.setPosition(vehicle.getMainStand(), toLoc);
            Messages.send(p, Messages.MAX_HELICOPTER_HEIGHT);
        }
        if(vehicle.getType() == VehicleType.CAR) {
            vehicle.setCurUpSpeed(0);
        }
        //Bukkit.broadcastMessage("CurUpSpeed: "+ vehicle.getCurUpSpeed() + " UpSpeed: " + vehicle.getUpSpeed() + " MaxUpSpeed: " + vehicle.getMaxUpSpeed() + " Velocity: " + vehicle.getMainStand().getVelocity().toString());
        vehicle.updatePositions();

        Random rand = new Random();
        int randint = rand.nextInt(100);
        if(randint == 4) {
            vehicle.setFuelLevel(vehicle.getFuelLevel() - 1);
            Methods.updateBar(p, vehicle.getFuelLevel() <= 10 ? BarColor.RED : vehicle.getFuelLevel() <= 75 ? BarColor.YELLOW : BarColor.GREEN, "Brandstof: " + vehicle.getFuelLevel() + "%", BarStyle.SOLID, vehicle.getFuelLevel() / 100f, true);

        }

    }

}

