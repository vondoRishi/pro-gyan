package util.mlUtil;



public class AttributeScore implements Comparable<AttributeScore> {
	private String mName;
	private int mAttributeIndex;
	private Double mScore;

	public AttributeScore(int pAttributeIndex, String pFtrId, Double pScore) {
		super();
		mAttributeIndex = pAttributeIndex;
		mName = pFtrId;
		mScore = pScore;
	}

	public int compareTo(AttributeScore pFeature) {
		if (this.mScore < pFeature.mScore) {
			return 1;
		}
		return -1;
	}

	@Override
	public String toString() {
		return mName + ": " + mScore;
	}

	public String getName() {
		return mName;
	}

	public int getAttributeIndex() {
		return mAttributeIndex;
	}

	public Double getScore() {
		return mScore;
	}
}
