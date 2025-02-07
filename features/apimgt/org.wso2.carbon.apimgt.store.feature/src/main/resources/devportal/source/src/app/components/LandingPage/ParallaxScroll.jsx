/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import ReactSafeHtml from 'react-safe-html';
import classNames from 'classnames';
import { app } from 'Settings';

const styles = theme => ({
    parallax: {
        /* Set a specific height */
        minHeight: 200,

        /* Create the parallax scrolling effect */
        backgroundAttachment: 'fixed',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        backgroundSize: 'cover',
        position: 'relative',
        marginTop: theme.spacing.unit * 3,
        marginBottom: theme.spacing.unit * 3,
    },
    slideContentWrapper: {
        position: 'absolute',
        background: '#00000044',
        color: theme.palette.getContrastText('#000000'),
        top: theme.spacing.unit * 3,
        padding: theme.spacing.unit * 2,
        margin: '0 100px',
        alignItem: 'center',
    },
    slideContentTitle: {
        fontWeight: theme.typography.fontWeightLight,
        fontSize: theme.typography.h3.fontSize,
    },
    slideContentContent: {
        fontWeight: theme.typography.fontWeightLight,
        fontSize: theme.typography.body1.fontSize,
    },
});

function ParallaxScroll(props) {
    const { classes, theme, index } = props;
    const slide = theme.custom.landingPage.parallax.content[index];

    return (
        <React.Fragment>
            <div
                className={classes.parallax}
                style={{ backgroundImage: 'url("' + app.context + slide.src + '")' }}
            >
                <div className={classNames(classes.slideContentWrapper, 'slideContentWrapper')}>
                    <div className={classNames(classes.slideContentTitle, 'slideContentTitle')}>
                        <ReactSafeHtml html={slide.title} />
                    </div>

                    <div className={classes.slideContentContent}>
                        <ReactSafeHtml html={slide.content} />
                    </div>
                </div>
            </div>
        </React.Fragment>
    );
}

ParallaxScroll.propTypes = {
    classes: PropTypes.object.isRequired,
    index: PropTypes.object.isRequired,
    theme: PropTypes.object.isRequired,
};
export default withStyles(styles, { withTheme: true })(ParallaxScroll);
