import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;


public class GetArtistRecommendation {

	public static void main(String[] args)
	{
		//get json data for tags of target artist and
		//create target artist object
		//whose sub-objects includes tags and count of each tags
		Gson gson = new Gson();
		String targetArtistName = "Yann Tiersen";
//		targetArtistName = targetArtistName.replace(" ","%20");
		String targetArtistTag_url = "http://ws.audioscrobbler.com/2.0/?method=artist.gettoptags&artist=Yann%20Tiersen&api_key=131d46a2c421afb17ea9991797f81b10&format=json";
		ArtistGetTag target_artist = getArtistGetTag(gson, targetArtistName,
				targetArtistTag_url);
		target_artist.getUsefulTags();
		System.out.println("All path"+target_artist.totalPath());
		List<ArtistGetTag.Tag> targetArtistAllTags = target_artist.getToptags().getTags();
		
		List<ArtistGetTag> similar_artists_name = new ArrayList<ArtistGetTag>();
		int same = 0;
		int exists = 0;
		HashMap<ArtistGetTag, List<Integer>> sharedTag = new HashMap<ArtistGetTag, List<Integer>>();
		for(int tagindex = 0 ; tagindex <targetArtistAllTags.size();tagindex++ )
		{
			ArtistGetTag.Tag eachtag = targetArtistAllTags.get(tagindex);
			String tagName = eachtag.getName();
			tagName = tagName.replaceAll(" ", "%20");
			String tags_artists_json = getJSON("http://ws.audioscrobbler.com/2.0/?method=tag.gettopartists&tag="+tagName+"&api_key=131d46a2c421afb17ea9991797f81b10&format=json", 50000);
			TagGetArtist tag_artists;
			tag_artists = gson.fromJson(tags_artists_json, TagGetArtist.class);
			tag_artists.cleanArtists();
			List<TagGetArtist.Artist> tag_artists_name =tag_artists.getTopartists().getArtist();

			
			for(int i = 0;i<tag_artists_name.size();i++)
			{
				TagGetArtist.Artist eachArtists = tag_artists_name.get(i);
				if(eachArtists.getName().equals(targetArtistName))
					System.out.println(++same);
				else
				{
					ArtistGetTag similar_exist =  alreadyExist(similar_artists_name,eachArtists.getName());
					if(similar_exist ==null)
					{
						String similar_artist_name = eachArtists.getName().replace(" ", "%20");
						String similar_artist_url = "http://ws.audioscrobbler.com/2.0/?method=artist.gettoptags&artist="+similar_artist_name+"&api_key=131d46a2c421afb17ea9991797f81b10&format=json";
						ArtistGetTag similar_artist = getArtistGetTag(gson, targetArtistName,
								targetArtistTag_url);
						similar_artist.setArtist_name(eachArtists.getName());
						similar_artists_name.add(similar_artist);
						List<Integer> tagsshared= new ArrayList<Integer>();
						tagsshared.add(tagindex);
						sharedTag.put(similar_artist, tagsshared);
					}
					else
					{
						
						System.out.println(eachArtists.getName()+" exists:"+(++exists));
						List<Integer> tagsshared = (List<Integer>) sharedTag.get(similar_exist);
						tagsshared.add(tagindex);
						sharedTag.put(similar_exist, tagsshared);
					}
				}
			}
		}
		HashMap<ArtistGetTag, Double> Similar_artist_score = new HashMap<ArtistGetTag, Double>();
		List<ArtistGetTag> topArtists = new ArrayList<ArtistGetTag>();
		double minScore = -1;
		for(ArtistGetTag eachArtists:similar_artists_name)
		{
			double score;
			List<Integer> tags_shared_index = sharedTag.get(eachArtists);
			int numerator = 0;
			for(Integer index: tags_shared_index)
			{
				numerator = numerator+eachArtists.getTagCount(targetArtistAllTags.get(index)).getCount()*targetArtistAllTags.get(index).getCount();
			}
			numerator = numerator*2;
 			score = (double) numerator/(double)(target_artist.totalPath()*target_artist.totalPath()+eachArtists.totalPath()*eachArtists.totalPath());
 			eachArtists.setScore(score);
 			if(topArtists.size()<100)
 			{
 				int position = 0;
 				for(int i = 0;i<topArtists.size();i++)
 				{
 					if(score>topArtists.get(i).getScore())
 					{
 						position = i;
 						break;
 					}
 				}
 				topArtists.add(position, eachArtists);
 				minScore = topArtists.get(topArtists.size()-1).getScore();
 			}
 			else
 			if(minScore<score)
 			{
 				int position = 0;
 				for(int i = 0;i<topArtists.size();i++)
 				{
 					if(score>topArtists.get(i).getScore())
 					{
 						position = i;
 						break;
 					}
 				}
 				topArtists.add(position, eachArtists);
 				if(topArtists.size()>100) topArtists.remove(topArtists.size()-1);
 				minScore = topArtists.get(topArtists.size()-1).getScore();
 			}
		}
		for(ArtistGetTag topArtist:topArtists)
		{
			System.out.println(topArtist.getArtist_name()+"        "+topArtist.getScore());
		}
	}
	private static ArtistGetTag getArtistGetTag(Gson gson,
			String targetArtsitName, String targetArtistTag_url) {
		String targetartist_tags_json = getJSON(targetArtistTag_url, 50000);
		ArtistGetTag target_artist;
		target_artist = gson.fromJson(targetartist_tags_json, ArtistGetTag.class);
		target_artist.setArtist_name(targetArtsitName);
		return target_artist;
	}
	
	//get json data from url
	public static String getJSON(String url, int timeout) {
        URL u;
		try {
			u = new URL(url);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setRequestProperty("Content-length", "0");
	        c.setUseCaches(false);
	        c.setAllowUserInteraction(false);
	        c.setConnectTimeout(timeout);
	        c.setReadTimeout(timeout);
	        c.connect();
	        int status = c.getResponseCode();

	        switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                return sb.toString();
	        }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

    return null;
	}
	
	//check if an artist is already in the similar artists list
	public static ArtistGetTag alreadyExist(List<ArtistGetTag> similar_artists,String artist_candidate)
	{
		for(ArtistGetTag eachArtist: similar_artists)
		{
			if(artist_candidate.equals(eachArtist.getArtist_name()))
				return eachArtist;
		}
		return null;
	}
}
