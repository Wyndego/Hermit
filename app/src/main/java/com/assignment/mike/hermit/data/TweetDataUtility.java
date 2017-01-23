package com.assignment.mike.hermit.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.assignment.mike.hermit.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Class used to fake http fetch from the Twitter server. Generates test data to
 * used as though the HTTP call was complete and the data has been parsed into
 * the proper format from the fetched JSON.
 *
 * Created by Mike on 1/22/17.
 */
public class TweetDataUtility {

    private static final String LOG_TAG = TweetDataUtility.class.getSimpleName();
    private static final int MAX_HANDLE_LENGTH = 20;
    private static final int MAX_DISPLAY_NAME_LENGTH = 20;
    private static final int MAX_CONTENT_LENGTH = 135;

    public static final int MAX_TWEET_LENGTH = 140;
    public static final long WEEK_IN_MS = 1000 * 60 * 60 * 24 * 7;

    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;

    // Variables to simulate our current user...normally this would be handled via the login
    // and retrieval of a user account:
    public static final long CURRENT_USER_ID = 24601L;
    public static final String CURRENT_USER_HANDLE = "@gooduser";
    public static final String CURRENT_USER_DISPLAY_NAME = "Some Dood";
    public static final String CURRENT_USER_ICON = "CurrentUserIcon";


    private static final String[] ICON_LIST = new String[] {
            "Sun",
            "Moon",
            "Star",
            "Cat",
            "Dog",
            "Person",
            "Plane",
            "Circle"
    };


    public static ContentValues generateRandomTweet(long earliestPostTime) {
        Random rand = new Random();

        long currentTime = System.currentTimeMillis();
        long earliestTimestampToGenerate = currentTime - earliestPostTime;
        long timestamp = earliestPostTime + (Math.abs((long)rand.nextDouble()) * (currentTime - earliestTimestampToGenerate) );

        return generateTweet(
                Math.abs(rand.nextLong()),
                getRandomPartionOfTestData(MAX_DISPLAY_NAME_LENGTH).replaceAll("(\\r|\\n)", ""),
                "@" + getRandomPartionOfTestData(MAX_HANDLE_LENGTH).replaceAll("\\s+",""),
                ICON_LIST[rand.nextInt(ICON_LIST.length)],
                timestamp,
                getRandomPartionOfTestData(MAX_CONTENT_LENGTH),
                Math.abs(rand.nextLong() % 500L),
                Math.abs(rand.nextLong() % 500L),
                Math.abs(rand.nextLong() % 500L)
                );

    }

    public static ContentValues[] generateRandomTweets(long earliestPostTime) {
        Random rand = new Random();

        int numTweets = rand.nextInt(10);

        // Cover the first load so that there will be data.
        if (earliestPostTime < WEEK_IN_MS) {
            earliestPostTime = WEEK_IN_MS;
        }

        ContentValues[] resultArray = new ContentValues[numTweets];
        for (int i = 0; i < numTweets; i++) {
            resultArray[i] = generateRandomTweet(earliestPostTime);
        }

        return resultArray;
    }

    public static ContentValues generateTweet(long userId, String displayName, String handle,
                                              String iconLoc, long postTs, String content,
                                              long numLikes, long numReplies, long numRetweets) {
        ContentValues result = new ContentValues();

        result.put(TweetContract.TweetEntry.COLUMN_USER_ID, userId);
        result.put(TweetContract.TweetEntry.COLUMN_DISPLAY_NAME, displayName);
        result.put(TweetContract.TweetEntry.COLUMN_HANDLE, handle);
        result.put(TweetContract.TweetEntry.COLUMN_USER_ICON, iconLoc);
        result.put(TweetContract.TweetEntry.COLUMN_POST_TIMESTAMP, postTs);
        result.put(TweetContract.TweetEntry.COLUMN_CONTENT, content);
        result.put(TweetContract.TweetEntry.COLUMN_NUM_LIKES, numLikes);
        result.put(TweetContract.TweetEntry.COLUMN_NUM_REPLIES, numReplies);
        result.put(TweetContract.TweetEntry.COLUMN_NUM_RETWEETS, numRetweets);

        return result;
    }

    public static Tweet convertTweetConentValueToTweet(ContentValues tweet) {
        return new Tweet(tweet);
    }

    public static Tweet[] convertTweetValuesToTweetArray(ContentValues[] tweets) {
        if (tweets == null || tweets.length == 0) {
            return null;
        }

        Tweet[] result = new Tweet[tweets.length];
        for (int i = 0; i < tweets.length; i++) {
            result[i] = convertTweetConentValueToTweet(tweets[i]);
        }

        return result;
    }

    public static Tweet convertCursorRowToTweet(Cursor cursor) {
        Tweet result = new Tweet(cursor);

        return result;
    }

    // Simulates an image server where these user icons may be stored.
    public static int getUserIconReferenceFromString(String iconString) {
        int result = R.mipmap.ic_default_user;

        if (iconString == null || iconString.length() == 0) {
            return result;
        }

        if (iconString.equals("Sun")) {
            result = R.mipmap.ic_sun;
        } else if (iconString.equals("Moon")) {
            result = R.mipmap.ic_moon;
        } else if (iconString.equals("Star")) {
            result = R.mipmap.ic_star;
        } else if (iconString.equals("Cat")) {
            result = R.mipmap.ic_cat;
        } else if (iconString.equals("Dog")) {
            result = R.mipmap.ic_dog;
        } else if (iconString.equals("Person")) {
            result = R.mipmap.ic_person;
        } else if (iconString.equals("Plane")) {
            result = R.mipmap.ic_plane;
        } else if (iconString.equals("Circle")) {
            result = R.mipmap.ic_circle;
        } else if (iconString.equals(CURRENT_USER_ICON)) {
            result = R.mipmap.ic_launcher;
        }

        return result;
    }

    public static String formatTimeString(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - timestamp;

        if (difference < 0) {
            return "now";
        }

        if (difference < SECOND) {
            return "now";
        } else if (difference < MINUTE) {
            String value = Long.toString(difference/SECOND);
            return value + "s";
        } else if (difference < HOUR) {
            String value = Long.toString(difference/MINUTE);
            return value + "m";
        } else if (difference < DAY) {
            String value = Long.toString(difference/HOUR);
            return value + "h";
        } else {
            // Larger than 1 day ago...just return a formatted date.
            Date date = new Date(timestamp);
            return DateFormat.getDateInstance().format(date);
        }
    }

    private static final String getRandomPartionOfTestData(int maxLength) {
        Random rand = new Random();
        int length = rand.nextInt(maxLength) + 5;
        int startIndex = rand.nextInt(testDataLength - length);

        String result = testData.substring(startIndex, startIndex + length-1);
        return result;
    }


    private static final String testData = "Hello close fed cynically far a haltered gosh mowed duteously that by painful grotesque understood the slapped much much amidst aimless less far much hound alas mumbled some following changed egregiously darn robustly dissolutely that frugally one walrus far so alas well decisive twitched grizzly one according anathematic fragrantly after and hurt oafishly foretold.\n" +
            "Educational and dear and other removed tore yawned ahead urchin lax allegedly beside ocelot hilariously jeez growled until sheepish and black pugnacious sudden because off prudently some shed crud hyena pending while less agreeable tendentiously slid the tortoise tepid gosh mastodon caribou and less randomly before lost ashamedly more fittingly to gulped therefore desperate that more telepathic towards animated coy crud urchin after crud far because or much.\n" +
            "Crookedly sheepishly less darn pouting so much wetted grunted over sought reran by monkey that onto groomed hey adoringly and naked pessimistically ape reminantly out decorous a by terrier since rebukingly unjustifiable or childishly one belched ducked.\n" +
            "Barked shrank that resold a redoubtably and erratically much and and goodness goodness much jeez specially some richly less pert one flapped soberly unthinkingly this with up until so and while goldfish less.\n" +
            "Far some on a considering bandicoot a urchin and crucial because dear and staid gosh because much toucan much sporadic hazily shrugged as grouped lamely and armadillo where jeez weasel far or justly aerial chuckled far sobbed the much as wiped exited diplomatic sourly some this flamingo far far unicorn wildebeest so up less so suitable.\n" +
            "Fumed shined darn much flagrant tonally more and single-mindedly outsold quetzal kookaburra talkatively hen barring as red-handedly dear less blessedly far more yet hooted far after exorbitantly much or.\n" +
            "Cobra petted opposite some up regretfully alas uninhibited overdrew far vindictive infuriating flirted despite one obdurately oh untiringly and undertook yet one illicitly comparable a dear hello hey reined slid oh much withdrew beneath zebra hey pangolin staidly grew beneficent alas naked scorpion input diversely agreeably and prissily far where wherever before goodness regardless some.\n" +
            "Much more subconscious under madly jeez sadistic iguanodon hurried mongoose climbed meticulously this much fuzzy piously more racily buffalo since jeez up superbly tarantula past brought aboard however human toucan before sullenly the this as toward behind hawk kangaroo angelic hey and much stunning labrador much sharply the some a cheerful since ouch other jubilant overtook as darn sure built some ouch but dear dog thus jeepers and oh the ouch.\n" +
            "Contrary indefatigably astride out some therefore yikes eloquent falcon smelled cockily erratic onto jellyfish wow thinly the this badly compactly much ouch alas fanatically sloth during pouted far this while unbridled snapped oh this darn and after far that dearly.\n" +
            "Far wow sporadically easily bred lubber through brief scorpion and folded so one unlike however quaintly laconically discarded and true since otter before the measurably goodness some spitefully angelfish stolid exited far.\n" +
            "Gosh and confident and gosh wow far oh weasel much at that dolorous about contrary a much after shook a far menacingly far turtle a some flagrant adequate because supp far spoon-fed before that bled in that darn hey hippopotamus dizzily and one much or jeez in haggardly darn vociferous effusive hedgehog broke cassowary snickered yikes sang the dismissive this about mistaken overlay oh alas porcupine that yikes that terrier more ironic added hatchet hello woeful seagull.\n" +
            "Scratched before convincingly and nobly tiger extraordinarily ireful goodness amid nauseatingly far following pouted invoked goodness said this darn aside pill much since flipped stuck onto jeez owl that timidly hey mislaid alas some faulty far balked more kangaroo rhythmic gosh this out for sordidly gnu a then the telling some so bee far.\n" +
            "Jeez yikes flung besides a the one where crud darn before cassowary much ruthlessly much hey far hey much much gosh far far warthog ladybug woefully wombat overheard much flabbily jeez this hello careless viciously gecko jeepers tamarin and bowed spacious benign aside forecast gosh and curiously earthworm according packed yet however stretched.\n" +
            "One inside jeez jeez jubilantly versus babbled less improperly that crud that crud yikes jolly more jeez taut so sheep a notwithstanding far egregious oh tiger circa that.\n" +
            "Chose indecisive nutria well far much far wry more much one then yet porcupine patted the lynx in one accurate scorpion on that much shot indignantly more far powerless hence nutria awakened sent manta saucily wasp after over and a bluebird leered and grunted some admonishing this mischievous cat less read alas less kept a.\n" +
            "Idiotically a raccoon attentive black lorikeet re-laid disbanded muttered one besides hello oh beyond grimily less a bred hello python oh irresolutely much a by less some less irritable stoutly suggestively scratched much much porcupine markedly much notwithstanding petulantly regarding some darn crud freely yet apart porcupine walking leopard far up after much therefore dismissively burned less far a lizard this amused yikes oh artistically gosh haggard ouch one gosh upon rakish rampantly more outside under far.\n";

    private static final int testDataLength = testData.length();
}
