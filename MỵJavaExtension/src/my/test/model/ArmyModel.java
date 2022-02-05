package my.test.model;

public class ArmyModel {
	private int id;
	private String name;
	private String type;
	private int heal;
	private int strength;
	private int shield;
	
	
	public int getHeal() {
		return heal;
	}
	public void setHeal(int heal) {
		this.heal = heal;
	}
	public int getStrong() {
		return strength;
	}
	public void setStrong(int strong) {
		this.strength = strong;
	}
	public int getShield() {
		return shield;
	}
	public void setShield(int shield) {
		this.shield = shield;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
