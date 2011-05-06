// The beef of this function was lifted from the
// index everything example on https://github.com/rnewson/couchdb-lucene#readme

function(doc) {


    // Create a lucene search document
    var ret = new Document();
    log.debug("Indexing " + doc._id);

    function idx(obj, parentField) {
        for (var key in obj) {
            var fieldName = key;
            if (parentField && parentField.length > 0) {
                // the parentfield might be location then our field is unit, so we want
                // to index the unit as location_unit.  The reason we convert to _
                // is so that lucene can index it, lucene doesn't like fields with .s
                fieldName = parentField + "_" + fieldName;
            }
            // this is a test, store everything :D
            var store = "yes";
            var val = obj[key];
            switch (typeof val) {
                case 'object':
                    if (val && val.constructor === Array && typeof val[0] !== 'object') {
                        // it's a list of actual values, not objects
                        // collapse the list
                        ret.add(val.join(' '), {"field": fieldName, "store": store});
                    }
                    else {
                        // it's an object, so we need to index that too
                        idx(val, fieldName);
                    }
                    break;
                case 'function':
                    // don't index functions!
                    break;
                default:
                    // figure out if it's a date
                    // only set the type if it's a date
                    if (key.toLowerCase().indexOf('date') >= 0 && key.toLowerCase().indexOf('string') < 0) {
                        if (typeof val == 'number') {
                            val = new Date(val);
                        }
                        ret.add(val, {"field": fieldName, "store": store, "type": "date"});
                    }
                    else {
                        ret.add(val, {"field": fieldName, "store": store});
                    }
                    break;
            }
        }
    }

    ;

    idx(doc, "");

    // index the attachments, not sure how this is going to be used in the future.
    if (doc._attachments) {
        for (var i in doc._attachments) {
            ret.attachment("default", i);
        }
    }


    return ret;
}