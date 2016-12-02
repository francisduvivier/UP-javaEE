package system.repositories;

import annotations.SystemAPI;

/**
 * Deze enumklasse houdt de verschillende soorten machines bij.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public enum MachineType implements ResourceType {
	BLOOD_ANALYZER,
	SURGICAL_EQUIPMENT,
	ULTRASOUND_MACHINE,
	XRAY_SCANNER;
}
