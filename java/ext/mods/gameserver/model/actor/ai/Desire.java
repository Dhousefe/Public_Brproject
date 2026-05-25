/*
* Copyleft © 2024-2026 L2Brproject
* * This file is part of L2Brproject derived from aCis409/RusaCis3.8
* * L2Brproject is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the
* Free Software Foundation, either version 3 of the License.
* * L2Brproject is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
* * You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.gameserver.model.actor.ai;

/**
 * A datatype used as a simple "wish" for Npc.<br>
 * <br>
 * The weight is used to order the priority of the related {@link Intention}.
 */
public class Desire extends Intention
{
	private double _weight;
	
	public Desire(double weight)
	{
		super();
		
		_weight = weight;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}
	
	@Override
	public int compareTo(Intention other)
	{
		if (other instanceof Desire otherDesire)
		{
			double weightCompare = Double.compare(otherDesire.getWeight(), getWeight());
			if (weightCompare != 0.0)
				return weightCompare > 0 ? 1 : weightCompare < 0 ? -1 : 0;
		}
		
		return super.compareTo(other);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
	
	@Override
	public String toString()
	{
		return "Desire [type=" + _type.toString() + " weight=" + _weight + "]";
	}
	
	public double getWeight()
	{
		return _weight;
	}
	
	public void setWeight(double weight)
	{
		_weight = weight;
	}
	
	public void addWeight(double value)
	{
		_weight = Math.min(_weight + value, Double.MAX_VALUE);
	}
	
	public void reduceWeight(double value)
	{
		_weight -= value;
	}
	
	public void autoDecreaseWeight()
	{
		switch (_type)
		{
			case ATTACK:
				_weight -= 6.6;
				break;
			
			case CAST:
				_weight -= 66000;
				break;
			
			case NOTHING:
				_weight -= 0.5;
				break;
		}
	}
}