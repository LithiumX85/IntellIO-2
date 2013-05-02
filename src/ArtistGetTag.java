import java.util.List;


public class ArtistGetTag {

	private String artist_name;
	private TopTags toptags;
	private double score;

	public ArtistGetTag()
	{
		super();
	}
	public ArtistGetTag(String artist_name, TopTags toptags) {
		this.toptags = toptags;
		this.artist_name = artist_name;
	}
	
	//-------------------getter and setter-------------------//
	public String getArtist_name() {
		return artist_name;
	}
	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}
	public TopTags getToptags() {
		return toptags;
	}

	public void setToptags(TopTags toptags) {
		this.toptags = toptags;
	}
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	//-------------------Additional Functions-----------------//
	//clean the tag list to make sure all tags have count>0
	public void getUsefulTags()
	{
		List<Tag> alltags = this.toptags.getTags();
		int usefultags = 0;
		for(ArtistGetTag.Tag eachtag:alltags)
		{
			int tag_count = eachtag.getCount();
			if(tag_count<=0)
				break;
			usefultags++;
		}
		alltags = alltags.subList(0, usefultags);
		this.toptags.setTags(alltags);
		
	}
	//get all path from artist to its own
	public int totalPath()
	{
		int score = 0;
		List<Tag> tags= this.getToptags().getTags();
		for(Tag tag: tags)
		{
			score = score + tag.getCount()*tag.getCount();
		}
		return score;
	}
	//get count of a particular tag
	public Tag getTagCount(Tag tag)
	{
		List<Tag> tags = this.getToptags().getTags();
		for(Tag eachtag: tags)
		{
			if(eachtag.getName().equals(tag.getName()))
				return eachtag;
		}
		return null;
	}
	//--------------------TopTags Class---------------------//
	public class TopTags
	{
		private List<Tag> tag;
		public TopTags(List<Tag> tags)
		{
			this.tag = tags;
		}
		public List<Tag> getTags() {
			return tag;
		}
		public void setTags(List<Tag> tags) {
			this.tag = tags;
		}
	}
	
	//---------------------Tag Class-----------------------//
	public class Tag
	{
		private String name;
		private int count;
		private String url;

		public Tag(String name, int count, String url)
		{
			this.name = name;
			this.count =count;
			this.url = url;
		}
		public Tag()
		{
			super();
		}
		
		//-------------------getter and setter-----------------//
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
}
