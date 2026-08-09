package ext.mods.dolls;

public class Doll
{
	private final int _id;
	private final int _skillId;
	private final int _skillLvl;
	
	public Doll(int id, int skillId, int skillLvl)
	{
		_id = id;
		_skillId = skillId;
		_skillLvl = skillLvl;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public int getSkillLvl()
	{
		return _skillLvl;
	}
}

