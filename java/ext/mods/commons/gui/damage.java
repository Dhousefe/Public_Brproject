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
package ext.mods.commons.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ext.mods.gameserver.custom.data.BalanceData;
import ext.mods.gameserver.model.holder.BalanceHolder;
import ext.mods.gameserver.model.holder.BalanceName;

public class damage extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private final String[] columnNames = {
		"Attacker",
		"Target",
		"P.Atk",
		"M.Atk"
	};

	private final List<RowData> allData;
	private final List<RowData> data;

	private static class RowData {
		int _classAtk, _classTgt;
		BalanceHolder _modifier;

		RowData(int atk, int tgt, BalanceHolder mod) {
			_classAtk = atk;
			_classTgt = tgt;
			_modifier = mod;
		}
	}

	public damage() {
		allData = new ArrayList<>();
		data = new ArrayList<>();
		for (String key : BalanceData.getInstance().getModifierMap().keySet()) {
			String[] split = key.split(":");
			int atk = Integer.parseInt(split[0]);
			int tgt = Integer.parseInt(split[1]);
			BalanceHolder mod = BalanceData.getInstance().getModifier(atk, tgt);
			RowData row = new RowData(atk, tgt, mod);
			allData.add(row);
			data.add(row);
		}
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 2 || col == 3;
	}

	@Override
	public Object getValueAt(int row, int col) {
		RowData r = data.get(row);
		return switch (col) {
			case 0 -> BalanceName.getName(r._classAtk);
			case 1 -> BalanceName.getName(r._classTgt);
			case 2 -> r._modifier._pAtkMod;
			case 3 -> r._modifier._mAtkMod;
			default -> null;
		};
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		RowData r = data.get(row);
		try {
			double val = Double.parseDouble(value.toString());
			switch (col) {
				case 2 -> r._modifier._pAtkMod = val;
				case 3 -> r._modifier._mAtkMod = val;
			}
			BalanceData.getInstance().updateModifier(r._classAtk, r._classTgt, r._modifier);
			fireTableCellUpdated(row, col);
		} catch (NumberFormatException e) {
			System.out.println("Invalid value entered: " + value);
		}
	}

	/**
	 * Filtra os dados da tabela com base no nome da classe atacante.
	 * @param text O texto a ser filtrado (ignora maiúsculas/minúsculas).
	 */
	public void filter(String text) {
		data.clear();
		if (text == null || text.trim().isEmpty()) {
			data.addAll(allData);
		} else {
			String lowerFilter = text.trim().toLowerCase();
			for (RowData r : allData) {
				String atkName = BalanceName.getName(r._classAtk).toLowerCase();
				if (atkName.startsWith(lowerFilter)) {
					data.add(r);
				}
			}
		}
		fireTableDataChanged();
	}
}