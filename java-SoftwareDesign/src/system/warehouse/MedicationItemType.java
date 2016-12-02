package system.warehouse;

import annotations.SystemAPI;

/**
 * Een enumklasse met de mogelijke types voor een medicijn
 */
public enum MedicationItemType implements CompositeType {
	ASPIRIN {
		@Override
		@SystemAPI
		public String toString() {
			return "Aspirin";
		}
	},
	VITAMINS {
		@Override
		@SystemAPI
		public String toString() {
			return "Vitamins";
		}
	},
	ACTIVATED_CARBON {
		@Override
		@SystemAPI
		public String toString() {
			return "Activated Carbon";
		}
	},
	SLEEPING_TABLETS {
		@Override
		@SystemAPI
		public String toString() {
			return "Sleeping Tablets";
		}
	},
	MISC {
		@Override
		@SystemAPI
		public String toString() {
			return "Misc";
		}
	};
}
