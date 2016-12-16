package checker.validator;

public class Order {

	public final int id;
	public final String location;
	public final int freq;
	public final int containers;
	public final int volumePerContainer;
	public final double legingTijd;
	public final int loc;
	public final long xCoord;
	public final long yCoord;
	final int volume;

	int[] orderCountDay = new int[5];
	boolean declined = false;

	public Order(int id, String location, int freq, int containers, int volumePerContainer, double legingTijd, int loc,
			long xCoord, long yCoord) {
		this.id = id;
		this.location = location;
		this.freq = freq;
		this.containers = containers;
		this.volumePerContainer = volumePerContainer;
		this.legingTijd = legingTijd;
		this.loc = loc;
		this.xCoord = xCoord;
		this.yCoord = yCoord;

		this.volume = (this.containers * this.volumePerContainer);
	}

	public boolean isValid(WarningCollector warnings) {
		int count = 0;
		for (int i = 0; i < 5; i++) {
			switch (this.orderCountDay[i]) {
			case 0:
				break;
			case 1:
				count++;
				break;
			default:
				warnings.addWarning(
						"!!! Order " + this.id + " is planned " + this.orderCountDay[i] + " times on " + getDay(i));
				return false;
			}

		}
		if (count == 0) {
			this.declined = true;
		} else {
			this.declined = false;
			if (count != this.freq) {
				warnings.addWarning("!!! Order " + this.id + " is planned " + count + " times in the week.\n"
						+ "       expected 0 or " + this.freq + " times.");
				return false;
			}

			switch (this.freq) {
			case 2:
				if ((this.orderCountDay[0] + this.orderCountDay[3] != 2)
						&& (this.orderCountDay[1] + this.orderCountDay[4] != 2)) {
					warnings.addWarning("!!! Order " + this.id + " isn't planned on the correct days.\n"
							+ "       planned on: " + getDaysPlanned() + "\n"
							+ "       supposed to be planned on either Mo_Th or Tu_Fr");
					return false;
				}
				break;
			case 3:
				if (this.orderCountDay[0] + this.orderCountDay[2] + this.orderCountDay[4] != 3) {
					warnings.addWarning(
							"!!! Order " + this.id + " isn't planned on the correct days.\n" + "       planned on: "
									+ getDaysPlanned() + "\n" + "       supposed to be planned on Mo_We_Fr");
					return false;
				}
				break;
			}

		}
		return true;
	}

	public String getDaysPlanned() {
		String s = null;
		for (int i = 0; i < 5; i++) {
			if (this.orderCountDay[i] > 0) {
				s = (s == null ? "" : new StringBuilder(String.valueOf(s)).append("_").toString()) + getDayShort(i);
			}
		}

		return s == null ? "Declined" : s;
	}

	public void initChecking() {
		for (int i = 0; i < 5; i++) {
			this.orderCountDay[i] = 0;
		}

		this.declined = true;
	}

	public static String getDay(int day) {
		switch (day) {
		case 0:
			return "Monday";
		case 1:
			return "Tuesday";
		case 2:
			return "Wednesday";
		case 3:
			return "Thursday";
		case 4:
			return "Friday";
		}
		return "NoDay";
	}

	public static String getDayShort(int day) {
		switch (day) {
		case 0:
			return "Mo";
		case 1:
			return "Tu";
		case 2:
			return "We";
		case 3:
			return "Th";
		case 4:
			return "Fr";
		}
		return "??";
	}
}
