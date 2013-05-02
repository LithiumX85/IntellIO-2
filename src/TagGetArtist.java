import java.util.List;


public class TagGetArtist {
	private String tag_name;
	private TopArtists topartists;
	
	public TagGetArtist(String tag_name, TopArtists topartists)
	{
		this.topartists = topartists;
		this.tag_name = tag_name;
	}
	
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}
	public TopArtists getTopartists() {
		return topartists;
	}
	public void setTopartists(TopArtists topartists) {
		this.topartists = topartists;
	}
	public void cleanArtists()
	{
		if(this.topartists.artist.size()>20)
			this.topartists.artist = this.topartists.artist.subList(0, 20);
		
	}
	public class TopArtists
	{
		private List<Artist> artist;
		public TopArtists(List<Artist> artist)
		{
			this.artist = artist;
		}
		public List<Artist> getArtist() {
			return artist;
		}
		public void setArtist(List<Artist> artist) {
			this.artist = artist;
		}
		
	}
	public class Artist
	{
		private String name;
		public Artist(String name)
		{
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
}
